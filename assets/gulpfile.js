var gulp = require('gulp');
var sass = require('gulp-sass');
var prefix = require('gulp-autoprefixer');
var minifyCSS = require('gulp-minify-css');
var rename = require('gulp-rename');
var watch = require('gulp-watch');
var react = require('gulp-react');
var prettify = require('gulp-jsbeautifier');
var jshint = require('gulp-jshint');
var livereload = require('gulp-livereload');
var uglify = require('gulp-uglify');
var flatten = require('gulp-flatten');
var webpack = require('webpack');


gulp.task('styles', function() {

  return gulp.src('src/components/**/*.scss')
    .pipe(sass({ includePaths: ['./src/components'], errLogToConsole: true, outputStyle: 'expanded' }))
    .pipe(flatten())
    .pipe(gulp.dest('src/css'));
});

gulp.task('minify-css', ['styles'], function() {

    return gulp.src(['src/css/*.css', '!src/css/libs/*.css'])
        .pipe(minifyCSS({keepBreaks:false}))
        .pipe(rename(function (path) {
            path.extname = ".min.css"
        })).pipe(flatten())
        .pipe(gulp.dest('src/css/min'))
        .pipe(livereload());
});

gulp.task('prettify-html', function() {
  return gulp.src('src/*.html')
    .pipe(prettify({indentSize: 4}))
    .pipe(gulp.dest('src/'))
});

gulp.task('react', function () {
    return gulp.src('src/**/*.jsx')
        .pipe(react())
        .pipe(flatten())
        .pipe(gulp.dest('src/javascript'));
});

gulp.task('js-build', function (callback) {
    return webpack(require('./webpack.config.js'), function () {
        if(err) throw new gutil.PluginError("webpack:build", err);
        gutil.log("[webpack:build]", stats.toString({
          colors: true
        }));
        callback();
    })
});

gulp.task('lint', function() {
  return gulp.src('src/javascript/*.js')
    .pipe(jshint())
    .pipe(jshint.reporter('default'));
});

gulp.task('watch', function() {
    gulp.watch(['src/components/**/*.scss', 'src/components/**/*.jsx'], ['styles', 'minify-css', 'react']);
});

gulp.task('default', ['watch']);
gulp.task('build', ['styles', 'minify-css']);


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

