var webpack = require('webpack');
var CommonsChunkPlugin = require("webpack/lib/optimize/CommonsChunkPlugin");
var path = require('path');

module.exports = {
  debug: true,
  devtool: '#inline-source-map',
  entry: {
    'resume.js': './src/resume.jsx',
    'requestAdvice.js': './src/requestAdvice.jsx',
    'settings.js': './src/settings.jsx'
  },
  output: {
    publicPath: '/javascript',
    path: path.join(__dirname, 'public', 'javascript'),
    filename: '[name]'
  },
  module: {
    loaders: [{
      test: /\.js|jsx$/,
      loaders: ['babel']
    }, ],
  },
  resolve: {
    alias: {},
    extensions: ['', '.js', '.jsx'],
    modulesDirectories: ["node_modules", "bower_components"]
  },
  plugins: [
    new CommonsChunkPlugin('base.js'),
    new webpack.HotModuleReplacementPlugin()
  ]
};
