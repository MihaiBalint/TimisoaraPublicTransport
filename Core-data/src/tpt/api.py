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


@app.route('/')
def do_api_root():
    return 'Welcome\n'


@app.route('/generate_device_id')
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
    except Exception as ex:
        logging.exception("Could not obtain unused device id.")
        return 'NONE\n'


@app.route('/post_times_bundle')
def do_post_times_bundle():

    return 'Thank you\n'


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8080)
