#!/usr/bin/env python
from __future__ import print_function

import csv

import tpt.db


def _compare_stop(cursor, stop_id, stop_extid, ext_title, title, shorter,
                  junction, lat, lng):
    try:
        lat = float(lat)
        lng = float(lng)
    except ValueError:
        lat = float(-1.0)
        lng = float(-1.0)

    existing = tpt.db.find_stop(cursor, stop_id)
    if existing[1] != title:
        print("Found stop with same ext_stopid and different title")
    if existing[2][0] != lat:
        print("Found stop with same ext_stopid but different lat")
    if existing[2][1] != lng:
        print("Found stop with same ext_stopid but different lng")
    if existing[3] == False:
        print("Found stop with same ext_stopid but with is_station=False")
    attr = existing[4]
    if attr["short_title"] != shorter:
        print("Found stop with same ext_stopid and different short_title")
    eattr = existing[5]
    if eattr["ext_title"] != ext_title:
        print("Found stop with same ext_stopid and different ext_title")
    return stop_id


def _insert_stop(cursor, stop_extid, ext_title, title, shorter, junction,
                 lat, lng):
    try:
        lat = float(lat)
        lng = float(lng)
    except ValueError:
        lat = float(-1.0)
        lng = float(-1.0)

    return tpt.db.insert_stop(
        cursor, title, lat, lng,
        short_title=shorter,
        ext_stopid=stop_extid,
        ext_title=ext_title)


def _compare_route(cursor, route_id, route_extid, ext_title, direction):
    existing_route = tpt.db.find_route(cursor, route_id)
    existing_line = tpt.db.find_line(cursor, existing_route[1])
    if existing_line[3]["ext_title"] != ext_title:
        print("Found routes with same extid_direction and diff line_ext_title")
    return route_id


def _insert_line(cursor, ext_title):
    trams = set(["Tv1", "Tv2", "Tv4", "Tv5", "Tv6", "Tv7", "Tv8", "Tv9"])
    trolleys = set(["Tb11", "Tb14", "Tb15", "Tb16", "Tb17", "Tb18", "Tb19"])
    metro = set(["M30", "M35", "M36", "M43", "M44", "M45"])
    express = set(["E1", "E2", "E3", "E4", "E4b", "E5", "E6", "E7", "E7b",
                   "E8", "E33"])
    buses = set(["3", "13", "13b", "21", "22", "28", "29", "32", "33",
                 "33b", "40", "46"])
    barred = set(["13b", "33b", "E4b"])
    if ext_title in trams:
        vehicle_type = 0
        title = ext_title[2:]
    elif ext_title in trolleys:
        vehicle_type = 1
        title = ext_title[2:]
    elif ext_title in metro:
        vehicle_type = 4
        title = ext_title
    elif ext_title in express:
        vehicle_type = 3
        title = ext_title
    elif ext_title in buses:
        vehicle_type = 2
        title = ext_title
    else:
        vehicle_type = 2
        title = ext_title
        print("Unknown vehicle {0} adding as bus.".format(ext_title))
    title = title if title[-1] != "b" else title[:-1]
    return tpt.db.insert_line(cursor, title, vehicle_type,
                              is_barred=ext_title in barred,
                              ext_title=ext_title)


def _insert_route_stop(cursor, route_id, stop_id, stop_index, is_invalid):
    is_enabled = {"true": False, "": True, "false": True}
    if is_invalid not in is_enabled:
        print("Found strage entry in invalid field.")
    return tpt.db.insert_route_stop(cursor, route_id, stop_id, stop_index,
                                    is_enabled[is_invalid])


def import_big_csv(data, cursor):
    route_dict = {}
    stop_dict = {}
    line_dict = {}
    route_stop_index = 0
    for row in csv.reader(data, delimiter=',', quotechar='"'):
        if len(row) == 0 or (len(row) == 1 and len(row[0].strip()) == 0):
            continue
        if len(row) > 0 and row[0].strip() == "LineID":
            continue
        if len(row) > 1 and len(row[0].strip()) == 0 and \
                len(row[1].strip()) == 0:
            continue
        stop_id = None
        route_id = None
        stop_title = row[4][:row[4].rfind("(")].strip()

        stop_extid = row[2].strip()
        if stop_extid in stop_dict:
            stop_id = _compare_stop(
                cursor, stop_dict[stop_extid], stop_extid, row[3].strip(),
                stop_title, row[5].strip(), row[6].strip(), row[7].strip(),
                row[8].strip())
        else:
            stop_id = _insert_stop(
                cursor,  stop_extid, row[3].strip(), stop_title,
                row[5].strip(), row[6].strip(), row[7].strip(), row[8].strip())
            stop_dict[stop_extid] = stop_id
        route_extid = row[0].strip()
        route_dir = row[4].split('(')[-1].split(")")[0].strip()
        route_key = "{0}_{1}".format(route_extid, route_dir)
        if route_key in route_dict:
            route_id = _compare_route(
                cursor, route_dict[route_key], route_extid, row[1].strip(),
                route_dir)
        else:
            ext_linetitle = row[1].strip()
            if ext_linetitle in line_dict:
                line_id = line_dict[ext_linetitle]
            else:
                line_id = _insert_line(cursor, ext_linetitle)
                line_dict[ext_linetitle] = line_id
            route_stop_index = 0
            route_id = tpt.db.insert_route(cursor, line_id, route_dir,
                                           ext_routeid=route_extid)

            route_dict[route_key] = route_id

        _insert_route_stop(cursor, route_id, stop_id, route_stop_index,
                           row[9].strip().lower())
        route_stop_index += 1
