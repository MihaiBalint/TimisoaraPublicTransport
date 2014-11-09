#!/usr/bin/env python
from __future__ import print_function

from fabric.api import local


def test():
    local("nosetests -c .noserc --with-coverage")
