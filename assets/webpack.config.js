var CommonsChunkPlugin = require("webpack/lib/optimize/CommonsChunkPlugin");
var path = require('path');

module.exports = {
    debug: true,
    devtool: '#inline-source-map',
    entry: {
        'main.js': './src/main.jsx'
    },
    output: {
        path: path.join(__dirname, 'src/javascript'),
        filename: '[name]'
    },
    module: {
        loaders: [
            {test: /\.js|jsx$/, loaders: ['babel']},
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