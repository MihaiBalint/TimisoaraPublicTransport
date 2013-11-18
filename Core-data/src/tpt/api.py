
from flask import Flask, url_for, request, jsonify
app = Flask(__name__)


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
    return 'Welcome'


@app.route('/generate_device_id')
def generate_device_id():

    return 'NONE\n'


@app.route('/post_times_bundle')
def post_times_bundle():

    return 'Thank you\n'


if __name__ == '__main__':
    app.run()
