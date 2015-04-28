// Karma configuration
// Generated on Tue Apr 21 2015 20:25:53 GMT+0530 (IST)
var webpack = require('webpack');

module.exports = function (config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
      // 'node_modules/react/dist/react-with-addons.js',
      'test/**/*.js',
      'test/**/*.jsx',
      'src/**/test/**/*.js',
      'src/**/test/**/*.jsx',
    ],


    // list of files to exclude
    exclude: [

    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
      'src/**/*.js': ['webpack'],
      'src/**/*.jsx': ['webpack'],
      'test/**/*.js': ['webpack'],
      'test/**/*.jsx': ['webpack']
    },
    webpack: {
      module: {
        loaders: [{
          test: /\.js|jsx$/,
          exclude: /lodash|node_modules/,
          loaders: ['babel']
        }, {
          test: /\.json$/,
          loader: 'json'
        }],
      },
      resolve: {
        extensions: ['', '.js', '.jsx', 'json'],
      },
      // externals: {
      //   react: "React",
      //   "react/addons": "React"
      // }, 
      plugins: [
        new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /en/)
      ] 
    },

    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress', 'junit'],

    junitReporter: {
      outputFile: './build/test-results/test-results.xml',
      suite: ''
    },
    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,
    captureTimeout: 150000,

    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['PhantomJS'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false
  });
};
