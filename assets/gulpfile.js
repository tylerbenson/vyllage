var gulp = require('gulp');
var sass = require('gulp-ruby-sass');
var prefix = require('gulp-autoprefixer');

// gulp.task('default', function () {
//     gulp.src('src/css/index.scss')
//         .pipe(sass({sourcemap: true, style: 'compact'}))
//         .pipe(prefix("last 1 version", "> 1%", "ie 8", "ie 7"))
//         .pipe(gulp.dest('dist'));
// });

gulp.task('styles', function() {
  return gulp.src('src/css/index.scss')
    .pipe(sass({ style: 'expanded' }))
    .pipe(autoprefixer('last 2 version', 'safari 5', 'ie 8', 'ie 9', 'opera 12.1', 'ios 6', 'android 4'))
    .pipe(gulp.dest('dist/assets/css'))
    .pipe(rename({suffix: '.min'}))
    .pipe(minifycss())
    .pipe(gulp.dest('dist/assets/css'))
    .pipe(notify({ message: 'Styles task complete' }));
});