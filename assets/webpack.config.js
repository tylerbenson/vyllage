var webpack = require('webpack');
var path = require('path');

module.exports = {
    entry: {
        'main.js': './src/components/main/main.jsx'
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
    plugins: []
};