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
        <textarea className="flat" onChange={this.handleChange}>
            I could really use your assistance on giving me some career or resume advice. Do you think you could take a couple of minutes and look over this for me?

            Thanks,
            James
        </textarea>
      </div>
    );
  }
});

module.exports = FormBody;

