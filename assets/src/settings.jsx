var React = require('react');
var Header = require('./components/header');
var Footer = require('./components/footer');
var Settings = require('./components/settings');
require('./components/intercom');
React.render(<Settings />, document.getElementById('settings'));