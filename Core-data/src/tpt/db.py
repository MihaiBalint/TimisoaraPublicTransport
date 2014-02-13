#!/usr/bin/env python
from __future__ import print_function

import contextlib
import psycopg2
import os

_schema_version = 2
_schema_template = "tpt%.4d"
_schema_name = _schema_template % _schema_version


class ItemNotFoundException(Exception):
    pass


def open_connection():
    host_port = os.environ.get("POSTGRES_HOST", "localhost:5432")
    hp = host_port.split(":")
    if len(hp) == 1:
        host = hp
        port = "5432"
    else:
        host = hp[0]
        port = hp[1]

    db = os.environ.get("POSTGRES_DB", "py_db")
    user = os.environ.get("POSTGRES_USER", "py_user")
    password = os.environ.get("POSTGRES_PASS", "py_pass")
    return psycopg2.connect(host=host, database=db, port=port,
                            user=user, password=password)


def exists_schema(cursor, schema_name):
    schema_exists = (
        "SELECT EXISTS(SELECT schema_name "
        "FROM information_schema.schemata "
        "WHERE schema_name = '%s');" % schema_name)
    cursor.execute(schema_exists)
    return cursor.fetchone()[0]


def exists_table(cursor, schema_name, table_name):
    table_exists = (
        "SELECT EXISTS(SELECT table_name "
        "FROM information_schema.tables "
        "WHERE table_schema = '%s' and table_name = '%s');" % (
            schema_name, table_name))
    cursor.execute(table_exists)
    return cursor.fetchone()[0]


def _create_tpt_schema(cursor):
    if exists_schema(cursor, _schema_name):
        return
    cursor.execute("CREATE SCHEMA %s;" % _schema_name)


def _create_table(cursor, table_name, ddl):
    if exists_table(cursor, _schema_name, table_name):
        return
    cursor.execute("CREATE TABLE %s.%s(%s);" %
                   (_schema_name, table_name, ", ".join(ddl)))


def _create_ids_table(cursor):
    ddl = [
        "device_id serial PRIMARY KEY",
        "sig varchar(1024) UNIQUE",
        "hash varchar(128) UNIQUE",
        "used boolean",
        "first_seen timestamp with time zone",
        "last_seen timestamp with time zone",
        ]
    _create_table(cursor, "device_ids", ddl)


def _create_times_table(cursor):
    ddl = [
        "log_id serial PRIMARY KEY",
        "device_id integer",
        "reported timestamp with time zone",
        "estimate1 varchar(32)",
        "estimate2 varchar(32)",
        "est_timestamp varchar(128)",
        "route_id varchar(32)",
        "station_id varchar(32)",
        "CONSTRAINT log_fk1 FOREIGN KEY (device_id) " \
            "REFERENCES %s.device_ids (device_id) MATCH SIMPLE " \
            "ON UPDATE CASCADE ON DELETE CASCADE" % _schema_name
        ]
    _create_table(cursor, "times_log", ddl)


def _create_routes_table(cursor):
    ddl = [
        "route_id serial PRIMARY KEY",
        "title varchar(32)",
        "vehicle_type integer",
        "is_barred boolean",
        "route_extid varchar(32)",
        ]
    _create_table(cursor, "routes", ddl)


def _create_stops_table(cursor):
    ddl = [
        "stop_id serial PRIMARY KEY",
        "title varchar(32)",
        "short_title varchar(32)",
        "gps_pos point",
        "is_station boolean",
        "stop_extid varchar(32)",
        ]
    _create_table(cursor, "stops", ddl)


def _create_route_stops_table(cursor):
    ddl = [
        "route_stop_id serial UNIQUE",
        "route_id integer",
        "stop_id integer",
        "stop_index integer",
        "is_enabled boolean",
        "CONSTRAINT route_stops_fk1 FOREIGN KEY (route_id) " \
            "REFERENCES %s.routes (route_id) MATCH SIMPLE " \
            "ON UPDATE CASCADE ON DELETE CASCADE" % _schema_name,
        "CONSTRAINT route_stops_fk2 FOREIGN KEY (stop_id) " \
            "REFERENCES %s.stops (stop_id) MATCH SIMPLE " \
            "ON UPDATE CASCADE ON DELETE CASCADE" % _schema_name,
        "PRIMARY KEY(route_id, stop_id, stop_index)"
        ]
    _create_table(cursor, "route_stops", ddl)


def create_database(connection):
    with contextlib.closing(connection.cursor()) as cursor:
        _create_tpt_schema(cursor)
        _create_ids_table(cursor)
        _create_times_table(cursor)
        _create_routes_table(cursor)
        _create_stops_table(cursor)
        _create_route_stops_table(cursor)


def drop_database(connection):
    with contextlib.closing(connection.cursor()) as cursor:
        cursor.execute("DROP SCHEMA %s CASCADE;" % _schema_name)


def insert_new_device(cursor, used=False):
    if used:
        sql = "insert into %s.device_ids (used, first_seen) " \
            "values (true, now()) returning device_id;"
    else:
        sql = "insert into %s.device_ids (used) values (false) " \
            "returning device_id;"

    cursor.execute(sql % _schema_name)
    return cursor.fetchone()[0]


def insert_device_sig(cursor, entry_id, device_sig, device_hash):
    sql = "update %s.device_ids set (sig, hash) = (%%s, %%s) " \
        "where device_id=%%s;"
    cursor.execute(sql % _schema_name, (device_sig, device_hash, entry_id))


def update_device_activity(cursor, device_hash):
    sql = "update %s.device_ids set last_seen = now() " \
        "where hash=%%s returning device_id;"
    cursor.execute(sql % _schema_name, (device_hash, ))
    result = cursor.fetchone()
    if result is None:
        raise ItemNotFoundException()
    return result[0]


def use_free_device_hash(cursor):
    sql = "select device_id, hash from %s.device_ids where used=false " \
        "order by device_id limit 1 for update;"
    cursor.execute(sql % _schema_name)
    result = cursor.fetchone()
    if result is None:
        return None
    device_id, device_hash = result
    sql = "update %s.device_ids set (used, first_seen) = " \
        "(true, now()) where device_id=%%s;"
    cursor.execute(sql % _schema_name, (device_id,))
    cursor.connection.commit()
    return device_hash


def insert_estimate(cursor, device_id, e1, e2, et, rid, sid):
    sql = "insert into %s.times_log (device_id, reported, estimate1, " \
        "estimate2, est_timestamp, route_id, station_id) " \
        "values (%%s, now(), %%s, %%s, %%s, %%s, %%s);"
    cursor.execute(sql % _schema_name, (device_id, e1, e2, et, rid, sid))


class PreviousDataNotFound(Exception):
    pass


def _migrate_data(cursor, previous_schema):
    _create_tpt_schema(cursor)
    cursor.execute("alter table %s.device_ids set schema %s;" % (
            previous_schema, _schema_name))
    cursor.execute(
        "create view %s.device_ids as select * from %s.device_ids;" %
        (previous_schema, _schema_name))
    cursor.execute("alter table %s.times_log set schema %s;" % (
            previous_schema, _schema_name))
    cursor.execute(
        "create view %s.times_log as select * from %s.times_log;" %
        (previous_schema, _schema_name))


def migrate_database(connection):
    with contextlib.closing(connection.cursor()) as cursor:
        if exists_schema(cursor, _schema_name):
            drop_database(connection)
    previous_schema = _schema_template % (_schema_version - 1)
    with contextlib.closing(connection.cursor()) as cursor:
        if not exists_schema(cursor, previous_schema):
            raise PreviousDataNotFound("Missing schema: %s " % previous_schema)
        _migrate_data(cursor, previous_schema)
    create_database(connection)

if __name__ == '__main__':
    pass
