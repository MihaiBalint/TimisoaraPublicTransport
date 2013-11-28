#!/usr/bin/env python
from __future__ import print_function

import contextlib
import StringIO
import unittest

import tpt.db
import tpt.tools
import tpt.db_test
import tpt.signed_ids_test


class DeviceIdGeneration(tpt.db_test.DatabaseSetup, unittest.TestCase):

    def setUp(self):
        super(DeviceIdGeneration, self).setUp()
        tpt.db.create_database(self.conn)
        tpt.signed_ids_test.gpg_key_setUp()

    def tearDown(self):
        super(DeviceIdGeneration, self).tearDown()
        tpt.signed_ids_test.gpg_key_tearDown()

    def test_generate_unused_device_id(self):
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.tools.generate_device_id(cursor, used=False)
            self.assertIsNotNone(tpt.db.use_free_device_hash(cursor))

    def test_generate_used_device_id(self):
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.tools.generate_device_id(cursor, used=True)
            self.assertIsNone(tpt.db.use_free_device_hash(cursor))

    def test_use_device_id(self):
        with contextlib.closing(self.conn.cursor()) as cursor:
            self.assertIsNone(tpt.db.use_free_device_hash(cursor))
            self.assertIsNotNone(tpt.tools.use_device_id(cursor))

    def test_generate_unused_device_ids(self):
        self.conn.commit()  # commit db creation

        with contextlib.closing(self.conn.cursor()) as cursor:
            self.assertIsNone(tpt.db.use_free_device_hash(cursor))
        tpt.tools.generate_unused_ids(self.conn, 5)
        with contextlib.closing(self.conn.cursor()) as cursor:
            for _ in range(5):
                self.assertIsNotNone(tpt.db.use_free_device_hash(cursor))
            self.assertIsNone(tpt.db.use_free_device_hash(cursor))

    def test_insert_times_log(self):
        data = """"3min", "15:25", "2013-11-29 00:17:49", "1111", "2222"
"4min", "15:28", "2013-11-29 00:17:49", "1112", "3333"

"""
        with contextlib.closing(self.conn.cursor()) as cursor:
            device_hash = tpt.tools.use_device_id(cursor)
            device_id = self._get_device_entry_id(cursor, device_hash)

            head = "192.168.0.253\r\n%s\r\n" % device_hash
            tpt.tools.insert_times_log(cursor, StringIO.StringIO(head + data))
            rt, e1, e2, et, rid, sid = self._get_times_log(cursor, device_id)
            self.assertEqual(
                (e1, e2, et, rid, sid),
                ("3min", "15:25", "2013-11-29 00:17:49", "1111", "2222"))
