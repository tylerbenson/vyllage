var bower = require('gulp-bower');
var cache = require('gulp-cached');
var del = require('del');
var flatten = require('gulp-flatten');
var gulp = require('gulp');
var gutil = require('gulp-util');
var jshint = require('gulp-jshint');
var livereload = require('gulp-livereload');
var minifyCSS = require('gulp-minify-css');
var autoprefixer   = require('gulp-autoprefixer');
var path = require('path');
var prettify = require('gulp-jsbeautifier');
var rename = require('gulp-rename');
var runSequence = require('run-sequence');
var sass = require('gulp-sass');
var watch = require('gulp-watch');
var webpack = require('webpack');

// var argv = require('minimist')(process.argv.slice(2));

gulp.task('clean', function () {
  del(['./public', './build'], function (err) {
    console.log('cleaned build directories')
  })
});

gulp.task('bower', function () {
  return bower({
    'cmd': 'update'
  })
});

gulp.task('copy-images', function () {
  return gulp.src(['src/images/*'])
    .pipe(gulp.dest('public/images'));
});

gulp.task('copy-html', function () {
  return gulp.src(['src/*.html'])
    .pipe(gulp.dest('public'));
});

gulp.task('copy-fonts', function () {
  return gulp.src(['bower_components/ionicons/fonts/*'])
    .pipe(gulp.dest('public/fonts'));
});

gulp.task('copy', ['copy-images', 'copy-html', 'copy-fonts']);

gulp.task('styles', function () {
  return gulp.src(['src/**/*.scss'])
    .pipe(sass({
      includePaths: ['./src/components', 'bower_components'],
      errLogToConsole: true,
      outputStyle: 'expanded'
    }))
    .pipe(autoprefixer({
      browsers: ['last 2 versions', 'ie 9']
    }))
    .pipe(minifyCSS())
    .pipe(flatten())
    .pipe(gulp.dest('public/css'))
});

gulp.task('prettify-html', function () {
  return gulp.src('src/*.html')
    .pipe(cache('prettify-html'))
    .pipe(prettify({
      html: {
        braceStyle: "collapse",
        indentChar: " ",
        indentScripts: "keep",
        indentSize: 2,
        maxPreserveNewlines: 5,
        preserveNewlines: true,
        unformatted: ["a", "sub", "sup", "b", "i", "u"],
        wrapLineLength: 120
      }
    }))
    .pipe(gulp.dest('src/'))
});

gulp.task('prettify-js', function () {
  return gulp.src(['./*.json', './*.js'])
    .pipe(cache('prettify-js'))
    .pipe(prettify({
      js: {
        indentChar: " ",
        indentLevel: 0,
        indentSize: 2,
        jslintHappy: true,
        wrapLineLength: 120
      }
    }))
    .pipe(gulp.dest('.'))
});

gulp.task('react', function (callback) {
  var webpackConfig = require('./webpack.config.js');
  webpackConfig.plugins = webpackConfig.plugins.concat(
    new webpack.DefinePlugin({
      "process.env": {
        "NODE_ENV": JSON.stringify("production")
      }
    }),
    new webpack.optimize.DedupePlugin(),
    new webpack.optimize.UglifyJsPlugin({
      compress: { drop_console: true }
    })
  );
  return webpack(webpackConfig, function (err, stats) {
    if (err) {
      throw new gutil.PluginError("webpack:build", err);
    }
    gutil.log("[webpack:build]", stats.toString({
      colors: true
    }));
    callback();
  })
});

gulp.task('dev-react', function (callback) {
  var myconfig = require('./webpack.config.js');
  myconfig.watch = true;
  myconfig.debug = true;
  myconfig.devtool = '#inline-source-map';
  webpack(myconfig, function (err, stats) {
    if (err) {
      throw new gutil.PluginError("webpack:build", err);
    }
    gutil.log("[webpack:build]", stats.toString({
      colors: true
    }));
  })
  callback();
});

gulp.task('lint', function () {
  return gulp.src('src/**/*.js')
    .pipe(cache('lint'))
    .pipe(jshint())
    .pipe(jshint.reporter('default'));
});

gulp.task('watch', ['build'], function () {
  gulp.watch(['src/**/*.scss'], function () {
    runSequence('styles');
  });
  gulp.watch(['src/**/*.jsx', 'src/**/*.js'], function () {
    runSequence('react');
  });
  gulp.watch(['src/*.html', 'src/images/*'], function () {
    runSequence('prettify-html', 'copy');
  });
  gulp.watch(['./*.js', './*.json'], function () {
    runSequence('prettify-js');
  });
});

gulp.task('build', function () {
  runSequence('clean', 'bower', ['react', 'copy', 'styles']);
});

// dev-watch excludes the react/jsx compilation, allowing this to be done by the server.
gulp.task('dev-watch', ['dev-build'], function () {
  gulp.watch(['src/**/*.scss'], function () {
    runSequence('styles');
  });
  gulp.watch(['src/*.html', 'src/images/*'], function () {
    runSequence('prettify-html', 'copy');
  });
  gulp.watch(['./*.js', './*.json'], function () {
    runSequence('prettify-js');
  });
});

// dev-build excludes the react/jsx compilation, allowing this to be done by the server.
gulp.task('dev-build', function () {
  runSequence('clean', 'bower', 'dev-react', ['copy', 'styles']);
});

gulp.task('default', ['watch']);

// from https://github.com/spring-io/sagan/blob/master/sagan-client/gulpfile.js
