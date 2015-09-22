var React = require('react');
var Header = require('./components/header');
var Footer = require('./components/footer');
var ExportResume = require('./components/export');

React.initializeTouchEvents(true);
React.render(<ExportResume />, document.getElementById('export'));