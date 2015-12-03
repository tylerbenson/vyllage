var React = require('react');
var LoginButton = require('./components/buttons/login');
var RegisterButton = require('./components/buttons/register');

React.initializeTouchEvents(true);
React.render(<LoginButton />, document.getElementById('login'));
React.render(<RegisterButton />, document.getElementById('header-register'));
React.render(<RegisterButton />, document.getElementById('footer-register'));