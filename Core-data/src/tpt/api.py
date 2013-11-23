#!/usr/bin/env python

from flask import Flask, request, jsonify
app = Flask(__name__)

import tpt.db
import tpt.signed_ids


def generate_device_id(cursor, used=True):
    entry_id = tpt.db.insert_new_device(cursor, used=used)
    _, device_sig, device_hash = tpt.signed_ids.make_signatures(entry_id)
    tpt.db.insert_device_sig(cursor, entry_id, device_sig, device_hash)
    return device_hash


def get_unused_device_id(cursor):
    device_hash = tpt.db.use_free_device_hash(cursor)
    if device_hash is not None:
        return device_hash
    return generate_device_id(cursor)


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
def api_root():
    return 'Welcome\n'


@app.route('/generate_device_id')
def generate_device_id():

    return 'NONE\n'


@app.route('/post_times_bundle')
def post_times_bundle():

    return 'Thank you\n'


if __name__ == '__main__':
    app.run()
