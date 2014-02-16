#!/usr/bin/env python
from __future__ import print_function

import contextlib
import StringIO
import unittest

import tpt.db_import

import tpt.db_test


class RouteStopImport(tpt.db_test.DatabaseSetup, unittest.TestCase):

    def setUp(self):
        super(RouteStopImport, self).setUp()
        tpt.db.create_database(self.conn)

    def tearDown(self):
        super(RouteStopImport, self).tearDown()

    def _get_stop_count(self, cursor):
        sql = "select count(stop_id) from %s.stops;"
        cursor.execute(sql % self.schema_name)
        return cursor.fetchone()[0]

    def _get_route_count(self, cursor):
        sql = "select count(route_id) from %s.routes;"
        cursor.execute(sql % self.schema_name)
        return cursor.fetchone()[0]

    def test_import_empty_csv(self):
        data = ""
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.db_import.import_big_csv(StringIO.StringIO(data), cursor)
            self.assertEqual(self._get_stop_count(cursor), 0)
            self.assertEqual(self._get_route_count(cursor), 0)

    def test_import_malformed_csv(self):
        data1 = ""","",,""," ","","","","","","dup script","29.11.11","x" """
        data2 = """"LineID","LineName","StationID","RawStationName","FriendlyStationName","ShortStationName","JunctionName","Lat","Long","Invalid","Verified","Verification Date","Goodle Maps Link","Info comments" """
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.db_import.import_big_csv(StringIO.StringIO(data1), cursor)
            tpt.db_import.import_big_csv(StringIO.StringIO(data2), cursor)
            self.assertEqual(self._get_stop_count(cursor), 0)
            self.assertEqual(self._get_route_count(cursor), 0)

    def test_import_single_line_csv(self):
        data = """1207,"3",3106,"Gara de Nord 2tb","Gara de Nord (FZB)","Gara","Gara de Nord","45.750569","21.207921","","dup script","03.04.13","maps.google" """
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.db_import.import_big_csv(StringIO.StringIO(data), cursor)
            self.assertEqual(self._get_stop_count(cursor), 1)
            stop = tpt.db.find_stop(cursor, 1)
            self.assertEqual(stop[1], "Gara de Nord")
            self.assertEqual(stop[5], "3106")
            self.assertEqual(stop[6], "Gara de Nord 2tb")

            self.assertEqual(self._get_route_count(cursor), 1)
            route = tpt.db.find_route(cursor, 1)
            self.assertEqual(route[1], "3")
            self.assertEqual(route[2], 2)
            self.assertEqual(route[3], False)
            self.assertEqual(route[4], "1207")
            self.assertEqual(route[5], "3")

            stations = tpt.db.find_route_stations(cursor, 1)
            self.assertEquals(len(stations), 1)
            self.assertEquals(stations[0][0], 1)
            self.assertEquals(stations[0][1], 0)
            self.assertEquals(stations[0][2], "Gara de Nord")
            self.assertEquals(stations[0][5], "3106")
            self.assertEquals(stations[0][7], True)

    def test_import_duplicate_line_csv(self):
        data = """1207,"3",3106,"Gara de Nord 2tb","Gara de Nord (FZB)","Gara","Gara de Nord","45.750569","21.207921","","dup script","03.04.13","maps.google"
1207,"33",3106,"Gara de Nord 2tb1","Gara de Nord1 (FZB)","Gara1","Gara1 de Nord","45.450569","21.307921","","dup script","03.04.13","maps.google" """
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.db_import.import_big_csv(StringIO.StringIO(data), cursor)
            self.assertEqual(self._get_stop_count(cursor), 1)

            self.assertEqual(self._get_route_count(cursor), 1)
            route = tpt.db.find_route(cursor, 1)
            self.assertEqual(route[1], "3")

    def test_import_plain_csv(self):
        data = """
1207,"3",3620,"Dambovita_4","B-dul Dambovita / Dep. Tramvaie (FZB)","Dambovita","Dambovita","45.739824","21.195948","","dup script","03.04.13","maps.goog"
1207,"3",2920,"Opre Gogu_2","Strada Ardealului / Strada Martir Opre Gogu (FZB)","Opre Gogu","","45.736488","21.188501","","dup script","03.04.13","maps.goog"
1207,"3",6040,"Pacii","Strada Pacii (FZB)","Pacii","","45.732995","21.182858","","dup script","03.04.13","maps.go"
"""
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.db_import.import_big_csv(StringIO.StringIO(data), cursor)
            self.assertEqual(self._get_stop_count(cursor), 3)

            stop = tpt.db.find_stop(cursor, 1)
            self.assertEqual(stop[1], "B-dul Dambovita / Dep. Tramvaie")
            self.assertEqual(stop[5], "3620")

            stop = tpt.db.find_stop(cursor, 3)
            self.assertEqual(stop[2], "Pacii")
            self.assertEqual(stop[5], "6040")

            self.assertEqual(self._get_route_count(cursor), 1)

            stations = tpt.db.find_route_stations(cursor, 1)
            self.assertEquals(len(stations), 3)
