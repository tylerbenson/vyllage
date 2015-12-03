var webpack = require('webpack');
var CommonsChunkPlugin = require("webpack/lib/optimize/CommonsChunkPlugin");
var path = require('path');

module.exports = {
  entry: {
    'homepage.js': './src/homepage.jsx',
    'resume.js': './src/resume.jsx',
    'getFeedback.js': './src/getFeedback.jsx',
    'settings.js': './src/settings.jsx',
    'export.js': './src/export.jsx',
    'intercom.js': './src/components/intercom.js'
  },
  output: {
    publicPath: '/javascript',
    path: path.join(__dirname, 'public', 'javascript'),
    filename: '[name]'
  },
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
    alias: {},
    extensions: ['', '.js', '.jsx', 'json'],
    modulesDirectories: ["node_modules", "bower_components"]
  },
  plugins: [
    new CommonsChunkPlugin('base.js'),
    new webpack.HotModuleReplacementPlugin()
  ]
};
