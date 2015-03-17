var React = require('react');
var Header = require('./components/header');
var ResumeEditor = require('./components/resume/Editor');
var Profile = require('./components/profile/Profile');

React.render(<Profile />, document.getElementById('resume-contactInfo'));
React.render(<ResumeEditor />, document.getElementById('resume'));