var React = require('react');

var FormBody = React.createClass({
  render: function () {
    return (
      <div className = "messageContent">
        <p className="message-text">
          I could really use your assistance on giving me some career or resume advice. Do you think
          you could take a couple of minutes and look over this for me?
        </p>
        <br/>
        <p className="message-text">Thanks,</p>
        <p className="message-text">Nathan</p>
      </div>
    );
  }
});

module.exports = FormBody;

