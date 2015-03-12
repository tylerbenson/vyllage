var React = require('react');
var ContentEditable = require("react-contenteditable");
var Actions = require('./actions');

var FormBody = React.createClass({
  handleChange: function(e) {
    Actions.updateMessage(e.target.value);
  },
  render: function () {
    return (
      <div className = "messageContent">
        <ContentEditable html={this.props.message} onChange={this.handleChange}/>
      </div>
    );
  }
});

module.exports = FormBody;

