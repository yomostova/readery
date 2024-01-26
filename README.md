# Readery :books:
This is a minimalistic book tracker, a tool that helps find your books and keep an eye on the reading progress.
Main components are:
- Java Backend (Spring Boot, Spring Security)
- PostgreSQL database (integrated with Hibernate)
- Frontend with HTML/CSS templates processed by Thymeleaf 

## Getting started
**1. Clone the repository**
```bash
git clone https://github.com/yomostova/readery.git
```
**2. Download Open Library data to populate the database.**\
https://openlibrary.org/developers/dumps - we need **works dump** and **authors dump**

**3. Create a PostgreSQL database named 'readery'**
(Documentation on creating a database)[https://www.postgresql.org/docs/current/manage-ag-createdb.html]

**4. Update application.properties with your information**
- datadump.location.authors= _path to the downloaded authors dump_
- datadump.location.works= _path to the downloaded works dump_
- spring.datasource.username= _username for your postgresql database_
- spring.datasource.password= _password for the database_
- initDB.ebabled= set false by default. Change it to **true** to insert data into database for the first time or if you want to populate the database with new data.

**5. Run application**
```bash
./gradlew build
./gradlew run
```
Check out the website at http://localhost:8080/.


## Functionality
### 1. Sign up and log into your account
### 2. Search through a catalogue of books from Open Library
### 3. Track the status of your books
Set start and finished dates, rating, add books to your wishlist or set status to reading, read or unfinished. 
### 4. Look through the books you've interacted with on the home page


:small_blue_diamond: **current TODO**: add navigation by author (author's page)

