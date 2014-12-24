var gulp = require('gulp'),
	sass = require('gulp-ruby-sass'),
	prefix = require('gulp-autoprefixer'),
	minifyCSS = require('gulp-minify-css'),
	rename = require('gulp-rename'),
	watch = require('gulp-watch');

gulp.task('styles', function() {
  return gulp.src('src/css/*.scss')
    .pipe(sass({ style: 'expanded' }))
    .pipe(gulp.dest('dist/assets/css'));
});

gulp.task('minify-css', function() {
   return gulp.src('dist/assets/css/*.css')
    .pipe(minifyCSS({keepBreaks:true}))
    .pipe(rename('main.min.css'))
    .pipe(gulp.dest('dist'))
});

gulp.task('watch', function() {
    gulp.watch('src/css/*.scss', ['styles', 'minify-css']);
});

gulp.task('default', ['watch']);