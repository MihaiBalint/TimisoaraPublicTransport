#!/usr/bin/env python

import unittest

import tpt.api
import tpt.db_test


class APITests(tpt.db_test.DatabaseSetup, unittest.TestCase):

    def setUp(self):
        super(APITests, self).setUp()
        tpt.db.create_database(self.conn)
        self.conn.commit()
        self.app = tpt.api.app.test_client()

    def tearDown(self):
        self.conn.rollback()
        super(APITests, self).tearDown()

    def test_404_error(self):
        rv = self.app.get('/mumbo_jumbo')
        self.assertEqual(rv.status_code, 404)


if __name__ == '__main__':
    unittest.main()
