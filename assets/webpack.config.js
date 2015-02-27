var CommonsChunkPlugin = require("webpack/lib/optimize/CommonsChunkPlugin");
var path = require('path');

module.exports = {
  debug: true,
  devtool: '#inline-source-map',
  entry: {
    'main.js': './src/main.jsx',
    'register.js': './src/register.jsx',
    'requestAdvice.js': './src/requestAdvice.jsx'
  },
  output: {
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
  ]
};
