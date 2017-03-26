# Data Wrangling

This folder contains all of the sample code relating to Data Wrangling

----

##### If you wish to use this sample code there are a few things you must know: 

[XPath](https://www.w3schools.com/xml/xml_xpath.asp)

[Authentication Requirements (for auth.py)](http://pygsheets.readthedocs.io/en/latest/authorizing.html) 

- Run `auth.py` and copy the url from the program to browser and authorize the application with Google
- The url should give a verification code to be pasted back into the program
- Rename `client_secretxxx.json` to `client_secret.json`

##### and a few tools you must install:

Install qt5 (version 5.5) on your machine -

​	Mac : `brew install qt55 && brew link —force qt55`

​	Windows : http://doc.qt.io/qt-5/windows-support.html

Run this Command while in the Data Wrangling Folder - `sudo pip install -r requirements.txt`

----

Once you have authenticated auth.py with Google, you must go through and add the proper values for all of the parameters for your Data Wrangler.

In wrangle.py, you will need to specify your data model, and your data sources for wrangling. i.e. What to wrangle and from where.

In auth.py you will have to specify the name of the Google Workbook, and the Sheet in that Workbook.

In sync.py you will have to specify your Big Parser Account Details, and the File ID of your Grid. 

Also make sure that you add any data sources you want to wrangle into the enabled_data_sources file.

