var gulp = require('gulp');
var gutil = require('gulp-util');
var sass = require('gulp-sass');
var prefix = require('gulp-autoprefixer');
var minifyCSS = require('gulp-minify-css');
var rename = require('gulp-rename');
var watch = require('gulp-watch');
var prettify = require('gulp-jsbeautifier');
var jshint = require('gulp-jshint');
var livereload = require('gulp-livereload');
var uglify = require('gulp-uglify');
var flatten = require('gulp-flatten');
var webpack = require('webpack');
var zip = require('gulp-zip');
var del = require('del');
var assign = require('lodash.assign');
var runSequence = require('run-sequence');
var bower = require('gulp-bower');

var path = require('path');

gulp.task('clean', function () {
    del(['./public', './build'], function (err) {
        console.log('cleaned build directories')
    })
});

gulp.task('bower', function () {
    return bower({'cmd': 'update'})
});

gulp.task('copy-images', function () {
    return gulp.src(['src/images/*'])
        .pipe(gulp.dest('public/images'));
});

gulp.task('copy-html', function () {
    return gulp.src(['src/*.html'])
        .pipe(gulp.dest('public'));
});

gulp.task('copy', ['copy-images', 'copy-html']);

gulp.task('styles', function() {
  return gulp.src(['src/**/*.scss'])
    .pipe(sass({ includePaths: ['./src/components', 'bower_components'], errLogToConsole: true, outputStyle: 'expanded' }))
    .pipe(flatten())
    .pipe(gulp.dest('public/css'))
});

// gulp.task('minify-css', ['styles'], function() {
//     return gulp.src(['src/css/*.css', '!src/css/libs/*.css'])
//         .pipe(minifyCSS({keepBreaks:false}))
//         .pipe(rename(function (path) {
//             path.extname = ".min.css"
//         })).pipe(flatten())
//         .pipe(gulp.dest('src/css/min'))
//         .pipe(livereload());
// });

gulp.task('prettify-html', function() {
  return gulp.src('src/*.html')
    .pipe(prettify({indentSize: 4}))
    .pipe(gulp.dest('src/'))
});

gulp.task('react', function (callback) {
    return webpack(require('./webpack.config.js'), function (err, stats) {
        if(err) { throw new gutil.PluginError("webpack:build", err); }
        gutil.log("[webpack:build]", stats.toString({
          colors: true
        }));
        callback();
    })
});

gulp.task('lint', function() {
  return gulp.src('src/**/*.js')
    .pipe(jshint())
    .pipe(jshint.reporter('default'));
});

// Gulp tasks to build assets.jar
gulp.task('assets.jar', function () {
    gulp.src('./public/**/*')
        .pipe(rename(function (path) {
            if(path.extname === '.html') {
                path.dirname = "templates";
            } else {
                path.dirname = "static";
            }
        }))
        .pipe(zip('assets.jar'))
        .pipe(gulp.dest('build/libs'));
})

gulp.task('watch', ['build'], function() {
    gulp.watch(['src/**/*.scss'], function () {
        runSequence('styles', 'assets.jar');
    });
    gulp.watch(['src/**/*.jsx'], function () {
        runSequence('react', 'copy-js', 'assets.jar');
    });
    gulp.watch(['src/*.html', 'src/images/*'], function () {
        runSequence('copy', 'assets.jar');
    });
});

gulp.task('default', ['watch']);

gulp.task('build', function () {
    // react needs to be run before copy-js and assets.jar needs to be run after all tasks
    runSequence('bower', 'react', ['copy', 'styles'] , 'assets.jar');
});


// from https://github.com/spring-io/sagan/blob/master/sagan-client/gulpfile.js
//var gulpFilter = require('gulp-filter'),
//    cram = require('gulp-cram'),
//    uglify = require('gulp-uglify'),
//    bowerSrc = require('gulp-bower-src'),
//    sourcemaps = require('gulp-sourcemaps'),
//    cssmin = require('gulp-minify-css'),
//    gulp = require('gulp');
//
//var paths = {
//    run: 'src/run.js',
//    css: {
//        files: ['src/css/*.css'],
//        root: 'src/css'
//    },
//    assets: ['src/img*/**','src/*.txt','src/*.html','src/font*/**','src/css*/filterable-list.css'],
//    dest: './dist/'
//};
//
//
//// concat and minify CSS files
//gulp.task('minify-css', function() {
//    return gulp.src(paths.css.files)
//        .pipe(cssmin({root:paths.css.root}))
//        .pipe(gulp.dest(paths.dest+'css'));
//});
//
//// cram and uglify JavaScript source files
//gulp.task('build-modules', function() {
//
//    var opts = {
//        includes: [ 'curl/loader/legacy', 'curl/loader/cjsm11'],
//        excludes: ['gmaps']
//    };
//
//    return cram(paths.run, opts).into('run.js')
//        .pipe(sourcemaps.init())
//        .pipe(uglify())
//        .pipe(sourcemaps.write("./"))
//        .pipe(gulp.dest(paths.dest));
//});
//
//// copy main bower files (see bower.json) and optimize js
//gulp.task('bower-files', function() {
//    var filter = gulpFilter(["**/*.js", "!**/*.min.js"]);
//    return bowerSrc()
//        .pipe(sourcemaps.init())
//        .pipe(filter)
//        .pipe(uglify())
//        .pipe(filter.restore())
//        .pipe(sourcemaps.write("./"))
//        .pipe(gulp.dest(paths.dest+'lib'));
//})
//
//// copy assets
//gulp.task('copy-assets', function() {
//    return gulp.src(paths.assets)
//        .pipe(gulp.dest(paths.dest));
//})
//
//gulp.task('build', ['minify-css', 'build-modules', 'copy-assets', 'bower-files'], function(){ });

