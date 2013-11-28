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


@app.route('/post_times_bundle', methods=["POST"])
def do_post_times_bundle():
    try:
        conn = tpt.db.open_connection()
        try:
            with contextlib.closing(conn.cursor()) as cursor:
                pass
        except:
            conn.rollback()
            raise
        finally:
            conn.close()
    except Exception:
        logging.exception("Error saving times bundle.")
    finally:
        return 'Thank you\n'


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8080)
