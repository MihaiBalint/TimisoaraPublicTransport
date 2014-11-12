#!/usr/bin/env python
from __future__ import print_function

import os
import unittest

import tpt.signed_ids


def setup_gpg(path):
    os.environ["GNUPGHOME"] = path

    
def gpg_key_setUp():
    setup_gpg(os.path.join(os.getcwd(), "test-data", "gnupg"))


def gpg_key_tearDown():
    del os.environ["GNUPGHOME"]


class SignedIdGeneration(unittest.TestCase):

    def setUp(self):
        gpg_key_setUp()

    def tearDown(self):
        gpg_key_tearDown()

    def test_generate_for_device_id(self):
        eid, sig, sig_hash = tpt.signed_ids.make_signatures("1")
        self.assertEqual(eid, "1")
        self.assertIsNotNone(sig)
        self.assertIsNotNone(sig_hash)
