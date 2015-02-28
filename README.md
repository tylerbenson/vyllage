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
npm start  // To start nodejs development server
gulp  // To build static assets
```

To build and run as a java app:

```
./gradlew :site:bootRun
```
