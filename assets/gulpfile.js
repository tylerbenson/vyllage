var gulp = require('gulp'),
    sass = require('gulp-sass'),
    prefix = require('gulp-autoprefixer'),
    minifyCSS = require('gulp-minify-css'),
    rename = require('gulp-rename'),
    watch = require('gulp-watch'),
    react = require('gulp-react'),
    prettify = require('gulp-jsbeautifier'),
    jshint = require('gulp-jshint'),
    livereload = require('gulp-livereload'),
    uglify = require('gulp-uglify'),
    flatten = require('gulp-flatten'),
    del = require('del');

gulp.task('clean', function () {
    del(['./build/*'], function (err) {
        console.log('cleaned build directory')
    })
});

gulp.task('copy', function () {
    gulp.src(['src/images/*'])
        .pipe(gulp.dest('build/images'))
    return gulp.src(['src/*.html'])
        .pipe(gulp.dest('build'));
});   

gulp.task('styles', function() {

  return gulp.src(['src/components/**/*.scss', 'src/base.scss'])
    .pipe(sass({ includePaths: ['./src/components', 'bower_components'], errLogToConsole: true, outputStyle: 'expanded' }))
    .pipe(flatten())
    .pipe(gulp.dest('build'))
    .pipe(livereload());
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
    return gulp.src('src/components/**/*.jsx')
        .pipe(react())
        .pipe(flatten())
        .pipe(gulp.dest('build/javascript'));
});

gulp.task('lint', function() {
  return gulp.src('src/javascript/*.js')
    .pipe(jshint())
    .pipe(jshint.reporter('default'));
});

gulp.task('watch', function() {
    gulp.watch(['src/**/*.scss'], ['styles']);
    gulp.watch(['src/**/*.scss'], ['styles']);
    gulp.watch(['src/*.html', 'src/images/*'], ['copy']);
});

gulp.task('default', ['watch']);
gulp.task('build', ['copy', 'styles', 'react']);


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

