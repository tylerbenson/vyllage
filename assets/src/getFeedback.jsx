var React = require('react');
var Header = require('./components/header');
var Footer = require('./components/footer');
var GetFeedback = require('./components/getFeedback');
require('./components/intercom');

React.initializeTouchEvents(true);
React.render(<GetFeedback />, document.getElementById('get-feedback'));