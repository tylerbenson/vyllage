var React = require('react');
var RecipientAdd = require('./RecipientAdd');
var RecipientList = require('./RecipientList');

var FormTo = React.createClass({
  getInitialState: function () {
    return {
      recipients: [
        {"firstName": "Tyler", "lastName": "Benson", email: "tyler.benson@vyllage.com" },
        {"firstName": "Nathan", "lastName": "Benson", email: "nathan.benson@vyllage.com" }
      ]
    };
  },
  addRecipient: function (recipient) {
    var recipients = this.state.recipients.concat(recipient);
    this.setState({recipients: recipients});
  },
  removeRecipient: function (index) {
    var recipients = this.state.recipients;
    recipients.splice(index, 1);
    this.setState({recipients: recipients});
  },
  render: function () {
    return (
        <div className='content-part'>
          <p className="rqst-key-word one column">to:</p>
          <div className='nine columns'>
             <RecipientAdd addRecipient={this.addRecipient} />
             <RecipientList recipients={this.state.recipients} removeRecipient={this.removeRecipient} />
          </div>
          <div className="two columns fb-button">
            <p className="small-text">ask your</p>
            <p className="big-text">facebook</p>
            <p className="small-text">friends</p>
          </div>
        </div>
    );
  }
});


module.exports = FormTo;