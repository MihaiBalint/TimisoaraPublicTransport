#!/usr/bin/env python
from __future__ import print_function

import contextlib
import datetime
import unittest

import tpt.db


def patch_schema(version=2):
    tpt.db._schema_version = version
    tpt.db._schema_template = "tpt_testing%.4d"
    test_schema = tpt.db._schema_template % tpt.db._schema_version
    tpt.db._schema_name = test_schema
    return test_schema


class DatabaseSetup(object):

    def setUp(self):
        self.schema_name = patch_schema()
        self.conn = tpt.db.open_connection()

    def tearDown(self):
        tpt.db.drop_database(self.conn)
        self.conn.commit()
        self.conn.close()

    def _get_device_entry_id(self, cursor, device_hash):
        sql = "select device_id from %s.device_ids where hash=%%s limit 1;"
        cursor.execute(sql % self.schema_name, (device_hash,))
        return cursor.fetchone()[0]

    def _get_times_log(self, cursor, device_id):
        sql = "select * from %s.times_log where device_id=%%s;"
        cursor.execute(sql % self.schema_name, (device_id, ))
        return cursor.fetchone()[2:]


class DatabaseCreation(DatabaseSetup, unittest.TestCase):

    def test_create(self):
        tpt.db.create_database(self.conn)
        with contextlib.closing(self.conn.cursor()) as cursor:
            self.assertTrue(tpt.db.exists_schema(cursor, self.schema_name))
            self.assertTrue(tpt.db.exists_table(cursor, self.schema_name,
                                                "device_ids"))
            self.assertTrue(tpt.db.exists_table(cursor, self.schema_name,
                                                "times_log"))

    def test_insert_device(self):
        tpt.db.create_database(self.conn)
        with contextlib.closing(self.conn.cursor()) as cursor:
            entry_id = tpt.db.insert_new_device(cursor)
            tpt.db.insert_device_sig(cursor, entry_id, "sig", "hash")
            self.assertEqual(self._get_device_entry_id(cursor, "hash"),
                             entry_id)

    def test_use_free(self):
        tpt.db.create_database(self.conn)
        with contextlib.closing(self.conn.cursor()) as cursor:
            device_hash = tpt.db.use_free_device_hash(cursor)
            self.assertIsNone(device_hash)

            entry_id = tpt.db.insert_new_device(cursor, used=False)
            tpt.db.insert_device_sig(cursor, entry_id, "sig0", "hash0")
            self.assertEqual(tpt.db.use_free_device_hash(cursor), "hash0")

            device_hash = tpt.db.use_free_device_hash(cursor)
            self.assertIsNone(device_hash)

    def test_use_free_locked(self):
        con2 = tpt.db.open_connection()
        tpt.db.create_database(self.conn)
        with contextlib.closing(self.conn.cursor()) as cursor, \
                contextlib.closing(con2.cursor()) as cursor2:
            entry_id1 = tpt.db.insert_new_device(cursor, used=False)
            tpt.db.insert_device_sig(cursor, entry_id1, "sig1", "hash1")
            self.conn.commit()

            self.assertEqual(tpt.db.use_free_device_hash(cursor2), "hash1")
            self.assertIsNone(tpt.db.use_free_device_hash(cursor))

            entry_id2 = tpt.db.insert_new_device(cursor, used=False)
            tpt.db.insert_device_sig(cursor, entry_id2, "sig2", "hash2")
            entry_id3 = tpt.db.insert_new_device(cursor, used=False)
            tpt.db.insert_device_sig(cursor, entry_id3, "sig3", "hash3")
            self.conn.commit()

            self.assertEqual(tpt.db.use_free_device_hash(cursor2), "hash2")
            self.assertEqual(tpt.db.use_free_device_hash(cursor), "hash3")

    def _get_last_seen(self, cursor, device_hash):
        sql = "select last_seen from %s.device_ids where hash=%%s;"
        cursor.execute(sql % self.schema_name, (device_hash, ))
        return cursor.fetchone()[0]

    def test_update_device_activity(self):
        tpt.db.create_database(self.conn)
        with contextlib.closing(self.conn.cursor()) as cursor:
            entry_id1 = tpt.db.insert_new_device(cursor, used=False)
            tpt.db.insert_device_sig(cursor, entry_id1, "sig1", "hash1")
            tpt.db.update_device_activity(cursor, "hash1")
            last_seen = self._get_last_seen(cursor, "hash1")
            now = datetime.datetime.now(last_seen.tzinfo)
            self.assertTrue((now - last_seen).total_seconds() <= 2)

    def test_update_non_existant_device_activity(self):
        tpt.db.create_database(self.conn)
        with contextlib.closing(self.conn.cursor()) as cursor:
            try:
                tpt.db.update_device_activity(cursor, "ZOMG!")
                self.fail()
            except tpt.db.ItemNotFoundException:
                pass

    def test_insert_estimate(self):
        tpt.db.create_database(self.conn)
        with contextlib.closing(self.conn.cursor()) as cursor:
            entry_id1 = tpt.db.insert_new_device(cursor, used=False)
            tpt.db.insert_device_sig(cursor, entry_id1, "sig1", "hash1")

            tpt.db.insert_estimate(cursor, entry_id1, "3min", "15:25",
                                   "2013-11-29 00:17:49", "1111", "2222")
            rt, e1, e2, et, rid, sid = self._get_times_log(cursor, entry_id1)
            self.assertEqual(
                (e1, e2, et, rid, sid),
                ("3min", "15:25", "2013-11-29 00:17:49", "1111", "2222"))

            now = datetime.datetime.now(rt.tzinfo)
            self.assertTrue((now - rt).total_seconds() <= 2)


class DatabaseMigration(DatabaseSetup, unittest.TestCase):

    def _insert_data(self):
        with contextlib.closing(self.conn.cursor()) as cursor:
            entry_id1 = tpt.db.insert_new_device(cursor, used=False)
            tpt.db.insert_device_sig(cursor, entry_id1, "sig1", "hash1")
            tpt.db.update_device_activity(cursor, "hash1")
            tpt.db.insert_estimate(cursor, entry_id1, "3min", "15:25",
                                   "2013-11-29 00:17:49", "1111", "2222")
            return entry_id1

    def _assert_data(self, entry_id1):
        with contextlib.closing(self.conn.cursor()) as cursor:
            self.assertEqual(self._get_device_entry_id(cursor, "hash1"),
                             entry_id1)
            rt, e1, e2, et, rid, sid = self._get_times_log(cursor, entry_id1)
            self.assertEqual(
                (e1, e2, et, rid, sid),
                ("3min", "15:25", "2013-11-29 00:17:49", "1111", "2222"))

    def tearDown(self):
        patch_schema(1)
        tpt.db.drop_database(self.conn)
        patch_schema(2)
        tpt.db.drop_database(self.conn)
        self.conn.commit()
        self.conn.close()

    def test_migrated_data(self):
        # create old data
        patch_schema(1)
        tpt.db.create_database(self.conn)
        entry_id1 = self._insert_data()

        # assert migrated data
        patch_schema(2)
        tpt.db.migrate_database(self.conn)
        self._assert_data(entry_id1)

    def test_legacy_reads_post_migration(self):
        # create old data
        patch_schema(1)
        tpt.db.create_database(self.conn)
        entry_id1 = self._insert_data()

        patch_schema(2)
        tpt.db.migrate_database(self.conn)
        # assert legacy data
        patch_schema(1)
        self._assert_data(entry_id1)

    def test_legacy_writes_post_migration(self):
        # migrate empty database
        patch_schema(1)
        tpt.db.create_database(self.conn)
        patch_schema(2)
        tpt.db.migrate_database(self.conn)

        # insert in legacy database
        patch_schema(1)
        entry_id1 = self._insert_data()

        # assert data in current database
        patch_schema(2)
        self._assert_data(entry_id1)
