#!/usr/bin/env python
from __future__ import print_function

from fabric.api import execute, local, sudo
from fabric.context_managers import settings
from termcolor import colored

import digitalocean
import json
import os
import sys


def error(message):
    print(colored(message, "red"))
    sys.stdout.flush()


def log(message):
    print(colored(message, "green"))
    sys.stdout.flush()


def info(message):
    print(colored(message, "blue"))
    sys.stdout.flush()


def install_test():
    local("pip install coverage==3.7.1")
    local("pip install ipython")
    local("pip install ipdb")
    local("pip install nose")


def test():
    local("nosetests -c .noserc --with-coverage")


def _get_manager_server_map():
    with open(".digitalocean.json", "rb") as config_file:
        config = json.loads(config_file.read())

    token = os.environ.get("DO_AUTH_TOKEN", config.get("auth_token"))
    domg = digitalocean.Manager(token=token)
    server_map = dict((dp.name, dp) for dp in domg.get_all_droplets())
    return domg, server_map


def _get_server(server_name):
    domg, server_map = _get_manager_server_map()
    return server_map[server_name] if server_name in server_map else \
        domg.get_droplet(name=server_name)


def deploy_test():
    error("Not implemented")


def deploy_live():
    error("Not implemented")


def servers():
    info("Servers:")
    domg, _ = _get_manager_server_map()
    for dodp in domg.get_all_droplets():
        info("\t{0}\t{1}\t{2}\t{3}".format(
            dodp.id, dodp.ip_address, dodp.name, dodp.status))


def poweron(server_name):
    dodp = _get_server(server_name)
    dodp.power_on()
    info("[{1}]: {0}".format(dodp.name, dodp.ip_address))


def _poweroff():
    sudo("poweroff now")


def poweroff(server_name):
    dodp = _get_server(server_name)
    with settings(password="x", connection_attempts=30):
        execute(_poweroff, hosts=[dodp.ip_address])
