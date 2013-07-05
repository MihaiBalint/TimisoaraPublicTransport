#!/usr/bin/python

from multiprocessing import Pool


def chunks(l, n):
    for i in xrange(0, len(l), n):
        yield l[i:i + n]


def map_reduce(data, mapper):
    pool = Pool(processes=8,)
    partitioned_data = list(chunks(data, len(data) / 8))
    mapping = pool.map(mapper, partitioned_data)
    


def access_frequency():
    pass


def hourly_frequency():
    pass


def quarter_hour_frequency():
    pass
