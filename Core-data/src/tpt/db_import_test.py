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
        self.conn.commit()
        self.tram_data = """
1266,"Tv4",3540,"Tv_Torontal 1","Calea Torontalului (Ciarda Rosie)","Torontalului","Torontalului / Dacia / Miresei","45.769007","21.219849","","dup script","15.07.11","x"
1266,"Tv4",3542,"Tv_M.Basarab 1","Bulevardul Cetatii / Pizeria San Marzano (Ciarda Rosie)","Cetatii","San Marzano","45.767849","21.216772","","dup script","15.07.11","x"
1266,"Tv4",3544,"Tv_Amforei 1","Strada Amforei (Ciarda Rosie)","Amforei","Amforei","45.765498","21.213076","","dup script","15.07.11","x"
"""

    def tearDown(self):
        self.conn.rollback()
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
            self.assertEqual(stop[5]["ext_stopid"], "3106")
            self.assertEqual(stop[5]["ext_title"], "Gara de Nord 2tb")

            self.assertEqual(self._get_route_count(cursor), 1)
            route = tpt.db.find_route(cursor, 1)
            line = tpt.db.find_line(cursor, route[1])
            self.assertEqual(line[1], "3")
            self.assertEqual(line[2], 2)
            self.assertEqual(line[3]["is_barred"], False)
            self.assertEqual(line[3]["ext_title"], "3")
            self.assertEqual(route[4]["ext_routeid"], "1207")

            stations = tpt.db.find_route_stations(cursor, 1)
            self.assertEquals(len(stations), 1)
            self.assertEquals(stations[0][0], 1)
            self.assertEquals(stations[0][1], 0)
            self.assertEquals(stations[0][2], "Gara de Nord")
            self.assertEquals(stations[0][5]["ext_stopid"], "3106")
            self.assertEquals(stations[0][6], True)

    def test_import_duplicate_line_csv(self):
        data = """1207,"3",3106,"Gara de Nord 2tb","Gara de Nord (FZB)","Gara","Gara de Nord","45.750569","21.207921","","dup script","03.04.13","maps.google"
1207,"33",3106,"Gara de Nord 2tb1","Gara de Nord1 (FZB)","Gara1","Gara1 de Nord","x","x","","dup script","03.04.13","maps.google" """
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.db_import.import_big_csv(StringIO.StringIO(data), cursor)
            self.assertEqual(self._get_stop_count(cursor), 1)

            self.assertEqual(self._get_route_count(cursor), 1)
            route = tpt.db.find_route(cursor, 1)
            line = tpt.db.find_line(cursor, route[1])
            self.assertEqual(route[2], "FZB")
            self.assertEqual(line[1], "3")

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
            self.assertEqual(stop[5]["ext_stopid"], "3620")

            stop = tpt.db.find_stop(cursor, 3)
            self.assertEqual(stop[5]["ext_title"], "Pacii")
            self.assertEqual(stop[5]["ext_stopid"], "6040")

            self.assertEqual(self._get_route_count(cursor), 1)

            stations = tpt.db.find_route_stations(cursor, 1)
            self.assertEquals(len(stations), 3)

    def test_active_lines_by_type(self):
        data = self.tram_data
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.db_import.import_big_csv(StringIO.StringIO(data), cursor)
            self.assertEqual(self._get_stop_count(cursor), 3)
            lines = tpt.db.find_active_lines_by_type(cursor, None, 0)

            self.assertEqual(len(lines), 1)
            self.assertEqual(lines[0][1], "4")
            self.assertEqual(lines[0][2], 0)
            self.assertEqual(lines[0][3]["ext_title"], "Tv4")

    def test_active_lines(self):
        data = self.tram_data
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.db_import.import_big_csv(StringIO.StringIO(data), cursor)
            self.assertEqual(self._get_stop_count(cursor), 3)
            lines = tpt.db.find_all_active_lines(cursor)
            self.assertEqual(len(lines), 1)
            self.assertEqual(lines[0][1], "4")

    def test_favorite_lines(self):
        data = self.tram_data
        with contextlib.closing(self.conn.cursor()) as cursor:
            tpt.db_import.import_big_csv(StringIO.StringIO(data), cursor)
            self.assertEqual(self._get_stop_count(cursor), 3)
            lines = tpt.db.find_favorite_lines(cursor)
            self.assertEqual(len(lines), 1)
            self.assertEqual(lines[0][1], "4")


if __name__ == '__main__':
    unittest.main()
