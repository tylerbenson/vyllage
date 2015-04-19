var React = require('react');
var Header = require('./components/header');
var Footer = require('./components/footer');
var AskAdvice = require('./components/askAdvice');
require('./components/intercom');

React.initializeTouchEvents(true);
React.render(<AskAdvice />, document.getElementById('ask-advice'));