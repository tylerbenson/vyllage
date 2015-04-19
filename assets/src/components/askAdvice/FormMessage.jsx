var React = require('react');
var Actions = require('./actions');
var Textarea = require('react-textarea-autosize');

var FormBody = React.createClass({
  handleChange: function(e) {
    Actions.updateMessage(e.target.value);
  },
  render: function () {
    return (
      <div className="message content">
        <h2>Message:</h2>
        <Textarea className="flat" rows="5" defaultValue={this.props.message} onChange={this.handleChange}></Textarea>
      </div>
    );
  }
});

module.exports = FormBody;

