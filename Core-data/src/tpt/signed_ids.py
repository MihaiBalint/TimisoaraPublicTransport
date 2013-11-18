#!/bin/env python
from __future__ import print_function

import hashlib
import sys
import subprocess


def make_signatures(plain_id):
    p = subprocess.popen(["gpg", "--clearsign", "--detach-sign"],
                         stdin=subprocess.PIPE, stdout=subprocess.PIPE)
    sig, _ = p.communicate(plain_id)
    sig_hash = hashlib.sha512(sig).hexdigest()
    return (plain_id, sig, sig_hash)

if __name__ == '__main__':
    print("Plain: {0}\nHash: {2}\nSig: {1}".format(
        make_signatures(sys.argv[1])))
