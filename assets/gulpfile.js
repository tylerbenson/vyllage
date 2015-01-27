var gulp = require('gulp'),
    sass = require('gulp-sass'),
	prefix = require('gulp-autoprefixer'),
	minifyCSS = require('gulp-minify-css'),
	rename = require('gulp-rename'),
	watch = require('gulp-watch'),
    react = require('gulp-react');

gulp.task('styles', function() {

  return gulp.src('src/sass/*.scss')
    .pipe(sass({ includePaths: ['./src/sass'], errLogToConsole: true, outputStyle: 'expanded' }))
    .pipe(gulp.dest('src/css'));
});

gulp.task('minify-css', ['styles'], function() {

    gulp.src('src/css/*.css')
        .pipe(minifyCSS({keepBreaks:false}))
        .pipe(rename(function (path) {
            path.extname = ".min.css"
        }))
        .pipe(gulp.dest('src/css/min'));
});

gulp.task('react', function () {
    return gulp.src('src/jsx/account-menu.jsx')
        .pipe(react())
        .pipe(gulp.dest('src/javascript'));
});

gulp.task('watch', function() {
    gulp.watch(['src/sass/*.scss', 'src/jsx/*.jsx'], ['styles', 'minify-css', 'react']);
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

