#!/usr/bin/env python
from __future__ import print_function

import contextlib
import json
import os.path
import sys
import unittest

import tpt.api
import tpt.db_test
import tpt.signed_ids_test


class APITests(tpt.db_test.DatabaseSetup, unittest.TestCase):

    def setUp(self):
        super(APITests, self).setUp()
        tpt.db.create_database(self.conn)
        self.conn.commit()
        tpt.signed_ids_test.gpg_key_setUp()
        self.app = tpt.api.app.test_client()
        self.sample_city_data = os.path.join("test-data", "linestations.csv")

    def tearDown(self):
        self.conn.rollback()
        super(APITests, self).tearDown()
        tpt.signed_ids_test.gpg_key_tearDown()

    def test_404_error(self):
        response = self.app.get("/mumbo_jumbo")
        self.assertEqual(response.status_code, 404)
        resp_json = json.loads(response.data)
        self.assertEqual(resp_json["status"], "error")
        self.assertIn("Not Found", resp_json["message"])

    def test_root_response(self):
        response = self.app.get('/')
        self.assertEqual(response.status_code, 200)
        resp_json = json.loads(response.data)
        self.assertEqual(resp_json["status"], "success")
        self.assertIn("Welcome", resp_json["message"])

    def test_generate_device_id(self):
        response = self.app.get('/generate_device_id')
        self.assertEqual(len(response.data), 128)
        self.assertNotIn("NONE", response.data)

    def test_generate_device_id_failure(self):
        tpt.signed_ids_test.setup_gpg("some_random_non_existing_dir")
        print("\n\n*** Expect GPG error here:", file=sys.stderr)
        response = self.app.get('/generate_device_id')
        print("\n\n*** We now return to your regullar programming.",
              file=sys.stderr)
        self.assertIn("NONE\n", response.data)

    def test_post_times_bundle(self):
        response = self.app.get('/generate_device_id')
        device_id = response.data
        data = "\n".join([
            "192.168.0.0",
            device_id,
            '"3min","15:24","2014-11-11 01:13:44","1111","2222"'])
        response = self.app.post('/post_times_bundle', data=data)
        self.assertEqual(response.data, "Thank you\n")

    def test_post_times_bundle_with_non_existing_device_id(self):
        data = "\n".join([
            "192.168.0.0",
            "some_non_existing_device_id",
            '"3min","15:24","2014-11-11 01:13:44","1111","2222"'])
        response = self.app.post('/post_times_bundle', data=data)
        self.assertEqual(response.status_code, 404)
        resp_json = json.loads(response.data)
        self.assertEqual(resp_json["status"], "error")
        self.assertIn("Not Found", resp_json["message"])

    def test_post_broken_times_bundle(self):
        response = self.app.get('/generate_device_id')
        device_id = response.data
        data = "\n".join([
            "192.168.0.0",
            device_id,
            '"3min","15:24","2014-11-11 01:13:44","{0}","22"'.format(
                "1" * 40)])
        response = self.app.post('/post_times_bundle', data=data)
        self.assertIn(response.data, "Not saved, thank you anyway\n")


class APIQueryTests(tpt.db_test.DatabaseSetup, unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        tpt.db_test.patch_schema()
        sample_city_data = os.path.join("test-data", "linestations.csv")
        conn = tpt.db.open_connection()
        tpt.db.create_database(conn)
        conn.commit()
        with contextlib.closing(conn.cursor()) as cursor, \
             open(sample_city_data, "rb") as csvfile:
            tpt.db_import.import_big_csv(csvfile, cursor)
            conn.commit()

    @classmethod
    def tearDownClass(cls):
        conn = tpt.db.open_connection()
        tpt.db.drop_database(conn)
        conn.commit()
        conn.close()

    def setUp(self):
        super(APIQueryTests, self).setUp()
        self.app = tpt.api.app.test_client()

    def tearDown(self):
        self.conn.rollback()

    def test_get_route_stops(self):
        response = self.app.get('/v1/routes/1/stops?stop_id&title&stop_index')
        resp_json = json.loads(response.data)
        self.assertEqual(resp_json["status"], "success")
        self.assertTrue(len(resp_json["stops"]) > 0)
        stop = resp_json["stops"][0]
        self.assertIn("stop_id", stop)
        self.assertIn("title", stop)
        self.assertIn("stop_index", stop)
        self.assertNotIn("short_title", stop)
        self.assertEqual(len(stop), 3)

    def test_get_route_with_fields(self):
        response = self.app.get('/v1/routes/2?route_id&title&direction')
        resp_json = json.loads(response.data)
        self.assertEqual(resp_json["status"], "success")
        self.assertEqual(len(resp_json["routes"]), 1)
        route = resp_json["routes"][0]
        self.assertIn("route_id", route)
        self.assertIn("title", route)
        self.assertNotIn("direction", route)
        self.assertEqual(len(route), 2)

    def test_get_route(self):
        response = self.app.get('/v1/routes/1')
        resp_json = json.loads(response.data)
        self.assertEqual(resp_json["status"], "success")
        self.assertEqual(len(resp_json["routes"]), 1)
        self.assertEqual(len(resp_json["routes"][0]), 0)

    def test_get_non_existing_route(self):
        response = self.app.get('/v1/routes/1111')
        resp_json = json.loads(response.data)
        self.assertEqual(resp_json["status"], "error")
        self.assertEqual(len(resp_json["routes"]), 0)
        self.assertIn("Not Found", resp_json["message"])

    def test_get_all_routes(self):
        response = self.app.get('/v1/routes')
        resp_json = json.loads(response.data)
        self.assertEqual(resp_json["status"], "success")
        self.assertTrue(len(resp_json["routes"]) > 0)


if __name__ == '__main__':
    unittest.main()
