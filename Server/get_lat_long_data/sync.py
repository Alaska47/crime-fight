import requests
import json
import csv
import numpy

BigParserAccountEmail = "neilpatelbigparser@gmail.com"
BigParserAccountPassword = "HackTJ2017"
FileIDFromGrid = "58d73444478af706507ad13c"

url = "https://www.bigparser.com/APIServices/api/common/login"
data = {
  "emailId": BigParserAccountEmail,
  "password": BigParserAccountPassword,
  "loggedIn": True
}
data_json = json.dumps(data)
headers = {'Content-type': 'application/json'}
authId = requests.post(url, data=data_json,headers=headers).json()['authId']
print(authId)
url = "https://www.bigparser.com/connectors-api/api/apps/file/googleDrive/false"
data = {
	"fileIDs" : [FileIDFromGrid]
}
data_json = json.dumps(data)
print(data_json)
headers = {'Content-type': 'application/json', 'authId':authId}

response = requests.put(url, data=data_json, headers=headers).json()
try:
	url = "https://www.bigparser.com/connectors-api/api/apps/file/googleDrive/" + response['requestId'] + "/status"
	headers = {'authId':authId}
	response = requests.get(url, headers=headers).json()
	print(response)
except KeyError:
	print("Your Grid is already synced up to the most recent version of your Google Sheet")


url2 = "https://www.bigparser.com/APIServices/api/grid/headers?gridId=58d73446478af70572adf982" 
try:
	headers = {'authId':authId}
	response = requests.get(url2, headers=headers).json()
	print(response)
except KeyError:
	print("Didn't work")

url3 = "https://www.bigparser.com/APIServices/api/query/table"
try:
	data2 = {
		"gridId": "58d73446478af70572adf982",
		"rowCount": 2042,
		"tags": [
			{
				"columnName": "lat"
			}
		]
	}
	data2_json = json.dumps(data2)
	headers = {'authId':authId, 'Content-type': 'application/json'}
	response = requests.post(url3, headers=headers, data=data2_json).json()
except KeyError:
	print("Didn't work")
