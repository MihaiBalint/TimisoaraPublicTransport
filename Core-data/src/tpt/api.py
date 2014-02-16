#!/usr/bin/env python

import contextlib
import logging
from flask import Flask, request, jsonify
app = Flask(__name__)

import tpt.db
import tpt.tools


@app.errorhandler(404)
def not_found(error=None):
    message = {
        'status': 404,
        'message': 'Not Found: ' + request.url,
        }
    resp = jsonify(message)
    resp.status_code = 404
    return resp


@app.route('/', methods=["GET", "POST"])
def do_api_root():
    return 'Welcome\n'


@app.route('/generate_device_id', methods=["GET", "POST"])
def do_generate_device_id():
    try:
        conn = tpt.db.open_connection()
        try:
            with contextlib.closing(conn.cursor()) as cursor:
                device_id = tpt.tools.use_device_id(cursor)
                conn.commit()
                return device_id
        except:
            conn.rollback()
            raise
        finally:
            conn.close()
    except Exception:
        logging.exception("Error getting unused device id.")
        return 'NONE\n'


@app.route('/post_times_bundle', methods=["POST", "GET"])
def do_post_times_bundle():
    try:
        conn = tpt.db.open_connection()
        try:
            with contextlib.closing(conn.cursor()) as cursor:
                tpt.tools.insert_times_log(cursor, request.stream)
        except:
            conn.rollback()
            raise
        finally:
            conn.close()
    except tpt.db.ItemNotFoundException:
        return not_found()
    except Exception:
        logging.exception("Error saving times bundle.")
        return 'Not saved, thank you anyway\n'
    else:
        return 'Thank you\n'


def _get_fields():
    fields = request.args.get('fields', "").split(",")
    return tuple(f.strip() for f in fields if f.strip())


def _map_routes(cursor, fields, routes):
    route_map = ["route_id", "title", "vehicle_type", "is_barred",
                 "route_extid", "route_exttitle"]
    departure = "departure"
    destination = "destination"
    for route in routes:
        item_dict = dict((k, v) for k, v in zip(route_map, route)
                         if k in fields)
        stations = tpt.db.find_route_stations(cursor, route[0])
        if departure in fields:
            item_dict[departure] = stations[0][2]
        if destination in fields:
            item_dict[destination] = stations[-1][2]
        yield item_dict


def _do_any_routes(route_gen):
    result = []
    status = "success"
    conn = tpt.db.open_connection()
    try:
        with contextlib.closing(conn.cursor()) as cursor:
            # TODO: paginate routes
            result = list(_map_routes(
                    cursor, _get_fields(), route_gen(cursor)))
    except:
        status = "error"
        raise
    finally:
        conn.close()
    return jsonify({
            "status": status,
            "routes": result})


def _mock_route_stops(fields):
    s1 = {"id": 123, "extid": "1234", "title": "Catedrala Mitropolitana",
          "next_eta": "1min"}
    s2 = {"id": 123, "extid": "1234", "title": "Pod C. Sagului",
          "next_eta": "2min"}
    result = {"status": "success", "stops": [s1, s2]}
    return jsonify(result)


@app.route('/v1/routes/<route_id>', methods=["GET"])
def do_routes(route_id):
    def routes_gen(cursor):
        if route_id:
            return [tpt.db.find_route(cursor, route_id)]
        else:
            return tpt.db.find_all_active_routes(cursor)
    return _do_any_routes(routes_gen)


@app.route('/v1/cities/<city_id>/tram/routes', methods=["GET"])
def do_tram_routes(city_id):
    def routes_gen(cursor):
        return tpt.db.find_active_routes_by_type(cursor, city_id, 0)
    return _do_any_routes(routes_gen)


@app.route('/v1/cities/<city_id>/trolleybus/routes', methods=["GET"])
def do_trollebus_routes(city_id):
    def routes_gen(cursor):
        return tpt.db.find_active_routes_by_type(cursor, city_id, 1)
    return _do_any_routes(routes_gen)


@app.route('/v1/cities/<city_id>/any_bus/routes', methods=["GET"])
def do_any_bus_routes(city_id):
    def routes_gen(cursor):
        buses = tpt.db.find_active_routes_by_type(cursor, city_id, 2)
        express = tpt.db.find_active_routes_by_type(cursor, city_id, 3)
        metro = tpt.db.find_active_routes_by_type(cursor, city_id, 4)
        return buses + express + metro
    return _do_any_routes(routes_gen)


@app.route('/v1/cities/<city_id>/favorite/routes', methods=["GET"])
def do_favorite_routes(city_id):
    def routes_gen(cursor):
        return tpt.db.find_favorite_routes(cursor)
    return _do_any_routes(routes_gen)


@app.route('/v1/route/<route_id>/stops', methods=["GET"])
def get_route_stops(route_id):
    fields = _get_fields()
    return _mock_route_stops(fields)


@app.route('/v1/route/<route_id>/eta', methods=["GET"])
def get_route_eta(route_id):
    fields = _get_fields()
    result = {"status": "error", "message": "Not implemented"}
    return jsonify(result)


@app.route('/v1/eta/<route_ext_id>/<stop_ext_id>', methods=["GET"])
def get_eta(route_ext_id, stop_ext_id):
    return jsonify({"eta": "7min"})


if __name__ == '__main__':
    app.debug = True
    app.run(host="0.0.0.0", port=8080)
