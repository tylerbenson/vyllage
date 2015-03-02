# Vyllage

## Initial Setup
* Install Java 8
* Install Node.js

To run an http server for just the static content and templates:

```
./gradlew httpd
gulp watch
```
or

To run development server with optimizations for build performance
```
cd assets
npm install
npm start  
gulp
open http://localhost:8000
```

To run development server without optimizations
```
cd assets
npm install
npm run-script httpd  
gulp watch
opens a browser window automatically
```

To build and run as a java app:

```
./gradlew :site:bootRun
```
