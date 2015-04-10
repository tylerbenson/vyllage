var webpack = require('webpack');
var CommonsChunkPlugin = require("webpack/lib/optimize/CommonsChunkPlugin");
var path = require('path');

module.exports = {
  debug: true,
  devtool: '#inline-source-map',
  entry: {
    'resume.js': './src/resume.jsx',
    'askAdvice.js': './src/askAdvice.jsx',
    'settings.js': './src/settings.jsx'
  },
  output: {
    publicPath: '/javascript',
    path: path.join(__dirname, 'public', 'javascript'),
    filename: '[name]'
  },
  module: {
    loaders: [
      {
        test: /\.js|jsx$/,
        exclude: /lodash|node_modules/,
        loaders: ['babel']
      },
      {test:/\.json$/, loader: 'json'}
   ],
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
