#!/usr/bin/env python
from __future__ import print_function

from fabric.api import local


def install_test():
    local("pip install coverage==3.7.1")
    local("pip install ipython")
    local("pip install ipdb")
    local("pip install nose")


def test():
    local("nosetests -c .noserc --with-coverage")
