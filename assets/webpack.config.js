var webpack = require('webpack');
var CommonsChunkPlugin = require("webpack/lib/optimize/CommonsChunkPlugin");
var path = require('path');

module.exports = {
    entry: {
        'main.js': './src/main.jsx'
    },
    output: {
        path: path.join(__dirname, 'public'),
        filename: '[name]'
    },
    module: {
        loaders: [
            {test: /\.jsx$/, loaders: ['babel']},
        ],
    },
    resolve : {
        alias: {},
        extensions: ['', '.js', '.jsx']
    },
    plugins: [
        new CommonsChunkPlugin('base.js'),
    ]
};