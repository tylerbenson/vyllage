[![Circle CI](https://circleci.com/gh/natebenson/vyllage.svg?style=svg&circle-token=094629387a9966730f9e7b4f904da02a05322c60)](https://circleci.com/gh/natebenson/vyllage)

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

To run development server with optimizations and mock api (Stopped using api server due to problems in protagonist)
```
cd assets
npm install
npm run dev-watch &
npm start
```
open [http://localhost:8080](http://localhost:8080)

To build and run as a java app:

```
./gradlew :site:bootRun &
cd assets
npm run watch &
```
To build and run using PostgreSQL

./gradlew -Dspring.profiles.active=prod :site:bootRun &
cd assets
npm run watch &

Changes to assets will still be picked up dynamically.

To simulate a production environment, install [foreman](https://github.com/ddollar/foreman) and run the following commands:

```
./gradlew clean stage
foreman start -p 8080
```
open [http://localhost:8080](http://localhost:8080)
