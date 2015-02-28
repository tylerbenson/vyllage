var express = require('express');
var path = require('path');
var fs = require('fs');
var protagonist = require('protagonist');
var resourceWalker = require('api-mock/lib/walker');
var walk = require('walk');
var app = express();

var walker = walk.walk(path.resolve('./api'), {
});

walker.on('file', function (root, fileStats, next) {
  var filePath = path.join(root, fileStats.name);
  var fileExt = path.extname(fileStats.name);
  if (fileExt === '.md') {
    fs.readFile(filePath, 'utf8', function (err, data) {
      if (err) { throw err };
      protagonist.parse(data, function(error, result) {
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

app.listen(8000);



