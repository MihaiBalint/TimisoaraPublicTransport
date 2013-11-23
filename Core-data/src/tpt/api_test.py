#!/usr/bin/env python
from __future__ import print_function

import contextlib
import unittest

import tpt.api
import tpt.db
import tpt.db_test


class DeviceIdGeneration(tpt.db_test.DatabaseSetup, unittest.TestCase):

    def test_generate_free_device_id(self):
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.api.generate_device_id(cursor, used=False)
            self.assertNotNone(tpt.db.use_free_device_hash(cursor))

    def test_generate_used_device_id(self):
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.api.generate_device_id(cursor, used=True)
            self.assertIsNone(tpt.db.use_free_device_hash(cursor))
