var React = require('react');
var Preview = require('./Preview');
var Edit = require('./Edit');

var Freeform = React.createClass({
  getDefaultProps: function () {
    return {
      section: {}
    };
  },
  render: function () {
    var uiEditMode = this.props.section.uiEditMode;
    return (
      <div></div>
    );
  }
});

module.exports = Freeform;