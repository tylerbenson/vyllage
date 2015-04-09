var React = require('react');
var Header = require('./components/header');
var Footer = require('./components/footer');
var ResumeEditor = require('./components/resume/Editor');

React.initializeTouchEvents(true);
React.render(<ResumeEditor />, document.getElementById('resume'));