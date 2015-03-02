var React = require('react');
var RecipientEdit = require('./RecipientEdit');
var RecipientList = require('./RecipientList');

var FormTo = React.createClass({
  getInitialState: function () {
    return {
      recipients: [
        {"firstName": "Tyler", "lastName": "Benson", email: "tyler.benson@vyllage.com" },
        {"firstName": "Nathan", "lastName": "Benson", email: "nathan.benson@vyllage.com" }
      ],
      selectedRecipient: null,
      recipient: {firstName: "", lastName: "", email: "", newRecipient: true}
    };
  },
  updateRecipient: function (recipient) {
    var recipients = this.state.recipients;
    if (this.state.selectedRecipient === null) {
      recipients = recipients.concat(recipient);
    } else {
      recipients[this.state.selectedRecipient] = recipient; 
    }
    this.setState({
      recipients: recipients,
      selectedRecipient: null,
      recipient: {firstName: "", lastName: "", email: "", newRecipient: true}
    });
  },
  removeRecipient: function (index) {
    var recipients = this.state.recipients;
    recipients.splice(index, 1);
    this.setState({
      recipients: recipients,
      selectedRecipient: null,
      recipient: {firstName: "", lastName: "", email: "", newRecipient: true}
    });
  },
  selectRecipient: function (index) {
    this.setState({
      selectedRecipient: index,
      recipient: this.state.recipients[index]
    });
  },
  render: function () {
    return (
        <div className='request-advice-form-to row'>
          <span className="one column rqst-key-word">to:</span>
          <div className='nine columns'>
            <RecipientEdit 
              updateRecipient={this.updateRecipient}
              recipient={this.state.recipient}
              selectedRecipient={this.state.selectedRecipient}
              />
            <RecipientList recipients={this.state.recipients} removeRecipient={this.removeRecipient} selectRecipient={this.selectRecipient} />
          </div>
          <div className="two columns fb-button">
            <span className="small-text">ask your</span><br/>
            <span className="big-text">facebook</span><br/>
            <span className="small-text">friends</span><br/>
          </div>
        </div>
    );
  }
});


module.exports = FormTo;