#!/bin/bash

echo "Creating Postgres user: py_user"
createuser -DRSP py_user

echo "Creating Postgres db: py_db with owner py_user"
createdb -O py_user py_db

echo "Creating Postgres plpythonu lang"
createlang plpythonu py_db

# This allows the user to _create_ procedural functions in plpython
echo "UPDATE pg_language SET lanpltrusted = true WHERE lanname = 'plpythonu';" | psql py_db
echo "GRANT ALL ON LANGUAGE plpythonu TO py_user;" | psql py_db
