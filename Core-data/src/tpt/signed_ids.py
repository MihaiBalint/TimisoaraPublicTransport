#!/usr/bin/env python
from __future__ import print_function

import hashlib
import sys
import subprocess


class SigningException(Exception):
    pass


def make_signatures(plain_id):
    p = subprocess.Popen(["gpg", "--clearsign", "--detach-sign"],
                         stdin=subprocess.PIPE, stdout=subprocess.PIPE)
    out, err = p.communicate(str(plain_id))
    if p.returncode != 0:
        raise SigningException()
    sig_hash = hashlib.sha512(out).hexdigest()
    return (plain_id, out, sig_hash)


if __name__ == '__main__':
    print("Plain: {0}\nHash: {2}\nSig: {1}".format(
        *make_signatures(sys.argv[1])))
