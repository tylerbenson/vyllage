var React = require('react');
var Actions = require('./actions');

var FormBody = React.createClass({
  handleChange: function(e) {
    Actions.updateMessage(e.target.value);
  },
  render: function () {
    return (
      <div className="message content">
        <h2>Message:</h2>
        <textarea className="flat" rows="5" defaultValue={this.props.message} onChange={this.handleChange}></textarea>
      </div>
    );
  }
});

module.exports = FormBody;

