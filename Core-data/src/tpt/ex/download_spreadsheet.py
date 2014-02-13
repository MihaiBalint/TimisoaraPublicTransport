#!/usr/bin/python

import csv
import getpass
import re
import sys
import urllib
import urllib2


class GoogleDocsClient(object):
    SPREADSHEET_URL = ("https://spreadsheets.google.com"
                       "/feeds/download/spreadsheets/Export"
                       "?key=%(doc_key)s"
                       "&exportFormat=%(format)s"
                       "&gid=%(sheet_id)i")

    def __init__(self, email, password):
        super(GoogleDocsClient, self).__init__()
        self.email = email
        self.password = password

    def get_auth_token(self):
        url = "https://www.google.com/accounts/ClientLogin"
        params = {
            "Email": self.email,
            "Passwd": self.password,
            "service": "wise",
            "accountType": "HOSTED_OR_GOOGLE",
            "source": "tpt.GoogleDocsClient"
        }
        req = urllib2.Request(url, urllib.urlencode(params))
        return re.findall(r"Auth=(.*)", urllib2.urlopen(req).read())[0]

    def download(self, doc_key, sheet_id=0, format="csv"):
        url = self.SPREADSHEET_URL % {
            "doc_key": doc_key,
            "format": format,
            "sheet_id": sheet_id}
        headers = {
            "Authorization": "GoogleLogin auth=" + self.get_auth_token(),
            "GData-Version": "3.0"
        }
        req = urllib2.Request(url, headers=headers)
        return urllib2.urlopen(req)

if __name__ == "__main__":
    spreadsheet_key = sys.argv[1]
    sheet_id = int(sys.argv[2])
    email = sys.argv[3]
    password = getpass.getpass()

    gs = GoogleDocsClient(email, password)
    csv_file = gs.download(spreadsheet_key, sheet_id)

    # Parse as CSV and print the rows
    for row in csv.reader(csv_file):
        print ", ".join(row)
