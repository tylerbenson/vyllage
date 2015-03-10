var express = require('express');
var path = require('path');
var fs = require('fs');
var protagonist = require('protagonist');
var resourceWalker = require('api-mock/lib/walker');
var logger = require('morgan');
var walk = require('walk');
var app = express();
var template = require('lodash.template');
var webpack = require('webpack');
var webpackMiddleware = require('webpack-dev-middleware');
var webpackConfig = require('./webpack.config.js');

app.use(logger('dev'));
app.use(webpackMiddleware(webpack(webpackConfig), {
  publicPath: '/javascript',
  lazy: false
}));
app.use('/images', express.static(path.join(__dirname, 'public/images')));
app.use('/css', express.static(path.join(__dirname, 'public/css')));

var walker = walk.walk(path.resolve('./api'), {});

walker.on('file', function (root, fileStats, next) {
  var filePath = path.join(root, fileStats.name);
  var fileExt = path.extname(fileStats.name);
  if (fileExt === '.md') {
    fs.readFile(filePath, 'utf8', function (err, data) {
      if (err) {
        throw err
      };
      protagonist.parse(data, function (error, result) {
        try {
          resourceWalker(app, result.ast['resourceGroups']);
        } catch (error) {
          console.log(error);
        }
      });
    })
  }
  next();
});

app.get('/:htmlName', function (req, res) {
  var htmlPath = path.join(__dirname, 'public', req.params.htmlName);
  fs.readFile(htmlPath, 'utf8', function (err, data) {
    if (err) {
      res.status(404).end();
    }
    res.send(data);
  })
})

app.get('/', function (req, res) {
  var htmlFiles = [];
  var html = '';
  var walker = walk.walk(path.resolve('./public'));
  walker.on('file', function (root, fileStats, next) {
    var filePath = path.join(root, fileStats.name);
    var fileExt = path.extname(fileStats.name);
    if (fileExt === '.html') {
      htmlFiles.push(fileStats.name);
    }
    next();
  });
  walker.on('end', function () {
    htmlFiles.forEach(function (name) {
      var compiled = template("<div><a href='\/${ name }'>${ name }</a></div>");
      html += compiled({
        name: name
      });
    });
    res.send(html);
  });
});

app.listen(8080);
