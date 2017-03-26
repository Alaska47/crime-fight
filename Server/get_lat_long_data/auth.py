#Developed by William Zhang 2017
import pygsheets
import sys
import csv

Google_Workbook = u''
Google_Sheet_in_Workbook = u''

def read_csv(ifile):
    with open(ifile) as file:
        reader = csv.reader(file, delimiter=',', quotechar='|')
        rows = []
        for row in reader:
            rows.append(row)
        return rows

def main(ifile):
    gc = pygsheets.authorize(
        outh_file="client_secret.json",
        outh_nonlocal=True)
    all_sheets = gc.list_ssheets()
    all_names = [sheet['name'] for sheet in all_sheets]
    sh = gc.open(Google_Workbook)
    wks = sh.worksheet_by_title(Google_Sheet_in_Workbook)
    to_insert = read_csv(ifile)
    for row in to_insert:
        rows = len(wks.get_col(1, returnas='cell', include_empty=False))
        wks.append_row(start=("A" + str(rows + 1)),
                       end=None, values=row)
if __name__ == "__main__":
    if len(sys.argv) == 2:
        main(sys.argv[1])
    else:
        # Input CSV filename, Output Google Spreadsheet name
 		 print("Error: Wrong number of arguments\nUsage: python auth.py <CSV filename>")