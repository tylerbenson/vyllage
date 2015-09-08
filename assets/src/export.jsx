var React = require('react');
var Header = require('./components/header');
var Footer = require('./components/footer');
var Exportresume = require('./components/export');


React.initializeTouchEvents(true);
React.render(<Exportresume />, document.getElementById('export'));