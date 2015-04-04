var React = require('react');
var Actions = require('./actions');
var firstName = document.getElementById('header-container').getAttribute('name');

var FormBody = React.createClass({
  handleChange: function(e) {
    Actions.updateMessage(e.target.value);
  },
  getMessage: function () {
    return "I could really use your assistance on giving me some career or resume advice. Do you think you could take a couple of minutes and look over this for me?\n\nThanks,\n" + firstName
  },
  render: function () {
    return (
      <div className="message content">
        <h2>Message:</h2>
        <textarea className="flat" rows="5" defaultValue={this.getMessage()} onChange={this.handleChange}></textarea>
      </div>
    );
  }
});

module.exports = FormBody;

