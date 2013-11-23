#!/bin/bash

echo "Creating Postgres user: py_user"
createuser -DRSP py_user

echo "Creating Postgres db: py_db with owner py_user"
createdb -O py_user py_db
