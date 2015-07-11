[![Circle CI](https://circleci.com/gh/natebenson/vyllage.svg?style=svg&circle-token=094629387a9966730f9e7b4f904da02a05322c60)](https://circleci.com/gh/natebenson/vyllage)

# Vyllage

## Initial Setup
* Install Java 8
* Install Node.js
* Install Java JCE 8

### Java JCE 8 
Either download and manually install from Oracle [Java Cryptography Extension (JCE) 8](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

Or use the following Debian compatible repository: 
[webupd8 oracle-java8-unlimited-jce-policy](http://www.ubuntuupdates.org/package/webupd8_java/lucid/main/base/oracle-java8-unlimited-jce-policy)


If you choose Oracle or have another OS, download the zip file, uncompress in some directory. Open a command prompt and move to the used directory. Execute the following commands, you might need admin authority to do this.

Linux

```
cp *.jar "$JAVA_HOME"/jre/lib/security
```

Windows 

```
cp *.jar "$JAVA_HOME"/jre/lib/security
```
 
---
To run http server without gulp optimizations

``` 
cd assets
npm install
npm run watch &
npm run httpd &
```
(opens [http://localhost:8080](http://localhost:8080) automatically)

or

To run development server with optimizations
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
