#!/usr/bin/env python
from __future__ import print_function

import argparse
import contextlib
import csv
import StringIO

import tpt.db
import tpt.signed_ids


def generate_device_id(cursor, used=True):
    entry_id = tpt.db.insert_new_device(cursor, used=used)
    _, device_sig, device_hash = tpt.signed_ids.make_signatures(str(entry_id))
    tpt.db.insert_device_sig(cursor, entry_id, device_sig, device_hash)
    return device_hash


def use_device_id(cursor):
    device_hash = tpt.db.use_free_device_hash(cursor)
    if device_hash is not None:
        return device_hash
    return generate_device_id(cursor)


def insert_times_log(cursor, data_stream):
    device_addr = data_stream.readline(100).strip()
    device_hash = data_stream.readline(140).strip()
    device_id = tpt.db.update_device_activity(cursor, device_hash)
    reader = csv.reader(data_stream, delimiter=",", quotechar='"',
                        lineterminator="\n", quoting=csv.QUOTE_ALL,
                        skipinitialspace=True)
    for row in reader:
        if len(row) != 5:
            continue
        strip_row = [e.strip() for e in row]
        tpt.db.insert_estimate(cursor, device_id, *strip_row)
    cursor.connection.commit()


def generate_unused_ids(conn, count):
    try:
        with contextlib.closing(conn.cursor()) as cursor:
            while count > 0:
                generate_device_id(cursor, used=False)
                count -= 1
                if (count % 100) == 0:
                    conn.commit()
    finally:
        conn.commit()


def _create_db(options, conn):
    tpt.db.create_database(conn)


def _drop_db(options, conn):
    tpt.db.drop_database(conn)


def _generate_batch(options, conn):
    generate_unused_ids(conn, options.batch_size)


def main_tools():
    parser = argparse.ArgumentParser(
        description="TPT data tools used to administer a TPT installation.")
    subparsers = parser.add_subparsers(title="TPT tools")

    parser_create = subparsers.add_parser(
        "create-db", help="create schema and tables; does not overwrite " \
            "or remove existing tables.")
    parser_create.set_defaults(func=_create_db)

    parser_drop = subparsers.add_parser(
        "drop-db", help="drop schema and tables and delete all data.")
    parser_drop.set_defaults(func=_drop_db)

    parser_gen_ids = subparsers.add_parser(
        "generate-batch",
        help="add a batch of unused device ids to avoid having to generate " \
            "them on-the-fly.")
    parser_gen_ids.add_argument(
        "batch_size", type=int, help="number of ready-to-use device ids " \
            "to generate")
    parser_gen_ids.set_defaults(func=_generate_batch)

    options = parser.parse_args()
    conn = tpt.db.open_connection()
    try:
        options.func(options, conn)
        conn.commit()
    except:
        conn.rollback()
        raise
    finally:
        conn.close()


if __name__ == '__main__':
    main_tools()
