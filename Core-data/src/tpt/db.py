#!/usr/bin/env python
from __future__ import print_function

import contextlib
import psycopg2
import os

_schema_version = 1
_schema_template = "tpt%.4d"
_schema_name = _schema_template % _schema_version


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


def _create_ids_table(cursor):
    ids_table = "device_ids"
    if exists_table(cursor, _schema_name, ids_table):
        return
    ddl = [
        "device_id serial PRIMARY KEY",
        "sig varchar(1024) UNIQUE",
        "hash varchar(128) UNIQUE",
        "used boolean",
        "first_seen timestamp with time zone",
        "last_seen timestamp with time zone",
        ]
    cursor.execute("CREATE TABLE %s.%s(%s);" %
                (_schema_name, ids_table, ", ".join(ddl)))


def _create_times_table(cursor):
    times_table = "times_log"
    if exists_table(cursor, _schema_name, times_table):
        return
    ddl = [
        "log_id serial PRIMARY KEY",
        "device_id integer",
        "reported timestamp with time zone",
        "estmate1 varchar(32)",
        "estmate2 varchar(32)",
        "est_timestamp varchar(128)",
        "route_id varchar(32)",
        "station_id varchar(32)",
        "CONSTRAINT log_fk1 FOREIGN KEY (device_id) " \
            "REFERENCES %s.device_ids (device_id) MATCH SIMPLE " \
            "ON UPDATE CASCADE ON DELETE CASCADE" % _schema_name
        ]
    cursor.execute("CREATE TABLE %s.%s(%s);" %
                (_schema_name, times_table, ", ".join(ddl)))


def create_database(connection):
    with contextlib.closing(connection.cursor()) as cursor:
        _create_tpt_schema(cursor)
        _create_ids_table(cursor)
        _create_times_table(cursor)


def drop_database(connection):
    with contextlib.closing(connection.cursor()) as cursor:
        cursor.execute("DROP SCHEMA %s CASCADE;" % _schema_name)


def get_device_entry_id(cursor, device_hash):
    # Mostly used for testing
    sql = "select device_id from %s.device_ids where hash=%%s limit 1;"
    cursor.execute(sql % _schema_name, (device_hash,))
    return cursor.fetchone()[0]


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


class PreviousDataNotFound(Exception):
    pass


def _migrate_data(cursor, previous_schema):
    pass


def migrate_database(connection):
    drop_database(connection)
    create_database(connection)
    previous_schema = _schema_template % (_schema_version - 1)
    with contextlib.closing(connection.cursor()) as cursor:
        if not exists_schema(cursor, previous_schema):
            raise PreviousDataNotFound("Missing schema: %s " % previous_schema)
        _migrate_data(cursor, previous_schema)

if __name__ == '__main__':
    pass
