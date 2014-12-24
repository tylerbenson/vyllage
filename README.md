# Vyllage

## Initial Setup
* Install Java 8
* Install Node.js

To run an http server for just the static content and templates:

```
./gradlew httpd
```
or

```
cd assets
npm install
npm run-script httpd
```

To build and run as a java app:

```
./gradlew :site:bootRun
```
