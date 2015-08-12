var React = require('react');
var Modal = require('../modal');

var ConfirmUnload = React.createClass({
  componentDidMount: function() {
    window.onbeforeunload = function() {
      return 'Your changes will not be saved.';
    };
    window.onunload = function() {
      this.props.onDiscardChanges();
    }.bind(this);
  },
  componentWillUnmount: function() {
    window.onbeforeunload = null;
  },
	render: function() {
		return null;
	}

});

module.exports = ConfirmUnload;