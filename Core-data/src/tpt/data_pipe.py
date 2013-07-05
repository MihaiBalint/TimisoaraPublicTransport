#!/usr/bin/python

# createuser -DEPRS tpt_db
# createdb -O tpt_db tpt_data
import psycopg2


class PersistentEntityType(object):

    def __init__(self, fields_dict, table_name):
        self.fields_dict = fields_dict
        self.ddl = ", ".join(
            "%s %s" % (field_name, field_type)
            for field_name, field_type in fields_dict.iteritems())
        self.table_name = table_name


class DataPipe(object):
    SCHEMA = "miba_1"

    def __init__(self, db, user, password, entity_types):
        self.con = None
        self.db = db
        self.user = user
        self.password = password
        self.entity_types = entity_types

    def connect_now(self):
        self._ensure_connection()
        self._ensure_database()

    def _ensure_connection(self):
        if self.con is not None:
            return
        self.con = psycopg2.connect(
            host="localhost", database=self.db,
            user=self.user, password=self.password)

    def _ensure_database(self):
        schema_exists = (
            "SELECT EXISTS(SELECT schema_name "
            "FROM information_schema.schemata "
            "WHERE schema_name = '%s');" % self.SCHEMA)
        table_exists = (
            "SELECT EXISTS(SELECT table_name "
            "FROM information_schema.tables "
            "WHERE table_schema = '%s' and table_name = '%%s');" % self.SCHEMA)
        cur = self.con.cursor()
        cur.execute(schema_exists)
        dummy_data = False
        if not cur.fetchone()[0]:
            cur.execute("CREATE SCHEMA %s;" % self.SCHEMA)
            dummy_data = True

        for et in self.entity_types:
            ddl = et.ddl % {"schema": self.SCHEMA}
            cur.execute(table_exists % et.table_name)
            if not cur.fetchone()[0]:
                cur.execute("CREATE TABLE %s.%s(%s);" %
                            (self.SCHEMA, et.table_name, ddl))
        if dummy_data:
            self._ensure_data()
        self.con.commit()

    def select(self, fields, entity_type, filters=(), orders=()):
        entity_fields = entity_type.fields_dict.keys()
        if not all(f in entity_fields for f in fields):
            # TODO Raise some exception
            pass
        statement = ("SELECT %(selection)s FROM %(schema)s.%(table)s "
                     "WHERE %(where)s")
        no_escape = {
            "selection": ", ".join(f for f in fields if f in entity_fields),
            "schema": self.SCHEMA,
            "table": entity_type.table_name}
        if len(filters) > 0:
            statement += " WHERE %s" % \
                " AND ".join("%s %s %%s" % (f, op) for f, op, _ in filters
                             if f in entity_fields)
        if len(orders) > 0:
            statement += " ORDER BY %s" % \
                ", ".join("%s %s" % (f, o) for f, o in orders
                          if f in entity_fields)
        statement = statement % no_escape
        cur = self.con.cursor()
        cur.execute(statement + ";", tuple(v for _, _, v in filters))
        return cur


class Device(object):
    def __init__(self):
        self.internal_id
        self.device_id
        self.register_date
        self.local_host
        self.source_host


class Estimate(object):
    def __init__(self):
        self.internal_id
        self.device_id
        self.local_host
        self.source_host
