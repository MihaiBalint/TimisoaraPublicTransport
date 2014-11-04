#!/usr/bin/env python
from __future__ import print_function

import contextlib
import psycopg2
import os

_schema_version = 3
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
        "last_seen timestamp with time zone"
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


def _create_lines_table(cursor):
    ddl = [
        "line_id serial PRIMARY KEY",
        "title varchar(32)"
        "vehicle_type integer",
        "attributes json",
        "CONSTRAINT related_routes_fk1 FOREIGN KEY (route_id) " \
            "REFERENCES %s.routes (route_id) MATCH SIMPLE " \
            "ON UPDATE CASCADE ON DELETE CASCADE" % _schema_name,
        "CONSTRAINT related_routes_fk2 FOREIGN KEY (related_id) " \
            "REFERENCES %s.routes (route_id) MATCH SIMPLE " \
            "ON UPDATE CASCADE ON DELETE CASCADE" % _schema_name
        ]
    _create_table(cursor, "lines", ddl)


def _create_routes_table(cursor):
    ddl = [
        "route_id serial UNIQUE",
        "line_id integer",
        "direction varchar(512)",
        "attributes json",
        "external_attributes json",
        "PRIMARY KEY(line_id, direction)",
        "CONSTRAINT routes_fk1 FOREIGN KEY (line_id) " \
            "REFERENCES %s.lines (line_id) MATCH SIMPLE " \
            "ON UPDATE CASCADE ON DELETE CASCADE" % _schema_name
        ]
    _create_table(cursor, "routes", ddl)


def _create_stops_table(cursor):
    ddl = [
        "stop_id serial PRIMARY KEY",
        "title varchar(512)",
        "gps_pos point",
        "is_station boolean",
        "attributes json",
        "external_attributes json"
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


def _create_schedules_table(cursor):
    ddl = [
        "route_schedules_id serial UNIQUE",
        "route_stop_id integer",
        "gross_applicability integer",
        "hour smallint",
        "minutes smallint[]",
        "attributes json",
        "CONSTRAINT route_schedules_fk1 FOREIGN KEY (route_stop_id) " \
            "REFERENCES %s.route_stops (route_stop_id) MATCH SIMPLE " \
            "ON UPDATE CASCADE ON DELETE CASCADE" % _schema_name,
        "PRIMARY KEY(route_stop_id, gross_applicability, hour)"
        ]
    _create_table(cursor, "route_schedules", ddl)


def create_database(connection):
    with contextlib.closing(connection.cursor()) as cursor:
        _create_tpt_schema(cursor)
        _create_ids_table(cursor)
        _create_times_table(cursor)

        _create_lines_table(cursor)
        _create_routes_table(cursor)
        _create_stops_table(cursor)
        _create_route_stops_table(cursor)
        _create_schedules_table(cursor)


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
        raise ItemNotFoundException(
            "Device not found {0}.".format(device_hash))
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


def insert_stop(cursor, title, shorter, lat, lng, ext_id, ext_title):
    sql = "insert into %s.stops (title, short_title, gps_pos, " \
        "is_station, stop_extid, stop_exttitle) " \
        "values (%%s, %%s, point(%%s, %%s), true, %%s, %%s) " \
        "returning stop_id;"
    cursor.execute(sql % _schema_name, (title, shorter, lat, lng,
                                        ext_id, ext_title))
    return cursor.fetchone()[0]


def find_stop(cursor, stop_id):
    sql = "select * from %s.stops where stop_id=%%s;"
    cursor.execute(sql % _schema_name, (stop_id, ))
    return cursor.fetchone()


def insert_route(cursor, title, vehicle_type, is_barred, ext_id, ext_title):
    sql = "insert into %s.routes (title, vehicle_type, is_barred, " \
        "route_extid, route_exttitle) values (%%s, %%s, %%s, %%s, %%s) " \
        "returning route_id;"
    cursor.execute(sql % _schema_name, (title, vehicle_type, is_barred,
                                        ext_id, ext_title))
    return cursor.fetchone()[0]


def find_route(cursor, route_id):
    sql = "select * from %s.routes where route_id=%%s;"
    cursor.execute(sql % _schema_name, (route_id, ))
    return cursor.fetchone()


def insert_route_stop(cursor, route_id, stop_id, stop_index, is_enabled):
    sql = "insert into %s.route_stops (route_id, stop_id, stop_index, " \
        "is_enabled) values (%%s, %%s, %%s, %%s) " \
        "returning route_stop_id;"
    cursor.execute(sql % _schema_name, (route_id, stop_id, stop_index,
                                        is_enabled))
    return cursor.fetchone()[0]


def find_route_stations(cursor, route_id):
    sql = "select s.stop_id, rs.stop_index, s.title, s.short_title, " \
        "s.gps_pos, s.stop_extid, s.stop_exttitle, rs.is_enabled " \
        "from {0}.stops as s, {0}.route_stops as rs where s.is_station and " \
        "s.stop_id=rs.stop_id and rs.route_id=%s order by rs.stop_index;"
    cursor.execute(sql.format(_schema_name), (route_id, ))
    return cursor.fetchall()


def find_active_routes_by_type(cursor, city_id, vehicle_type):
    sql = "select distinct r.* from {0}.route_stops as rs, {0}.routes as r " \
        "where r.vehicle_type=%s and rs.is_enabled and " \
        "rs.route_id=r.route_id order by r.title;"
    cursor.execute(sql.format(_schema_name), (vehicle_type, ))
    return cursor.fetchall()


def find_all_active_route(cursor):
    sql = "select distinct r.* from {0}.route_stops as rs, {0}.routes as r " \
        "where rs.is_enabled and rs.route_id=r.route_id order by r.title;"
    cursor.execute(sql.format(_schema_name))
    return cursor.fetchall()


def find_favorite_routes(cursor):
    sql = "select distinct r.* from {0}.route_stops as rs, {0}.routes as r " \
        "where rs.is_enabled and rs.route_id=r.route_id and " \
        "r.title=any(Array['4','E1','5','E2','2','6','8','7']) " \
        "order by r.title;"
    cursor.execute(sql.format(_schema_name))
    return cursor.fetchall()


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
