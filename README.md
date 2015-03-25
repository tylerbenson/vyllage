# Vyllage

## Initial Setup
* Install Java 8
* Install Node.js

To run http server without gulp optimizations

```
cd assets
npm install
npm run watch &
npm run httpd &
```
(opens [http://localhost:8080](http://localhost:8080) automatically)

or

To run development server with optimizations and mock api 
```
cd assets
npm install
npm run dev-watch &
npm start
```
open [http://localhost:8080](http://localhost:8080)

To build and run as a java app:

```
./gradlew site:bootRun &
cd assets
npm run watch &
```
To build and run using PostgreSQL

<<<<<<< HEAD
./gradlew -Dspring.profiles.active=prod :site:bootRun &
=======
./gradlew -Dspring.profiles.active=prod site:bootRun &
>>>>>>> branch 'uh-postgre-sql' of https://github.com/natebenson/vyllage.git
cd assets
npm run watch &

Changes to assets will still be picked up dynamically.

To simulate a production environment, install [foreman](https://github.com/ddollar/foreman) and run the following commands:

```
./gradlew clean stage
foreman start -p 8080
```
open [http://localhost:8080](http://localhost:8080)
