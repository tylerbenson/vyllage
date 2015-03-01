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
    console.log(recipient);
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
        <div className='row'>
          <p className="rqst-key-word one column">to:</p>
          <div className='nine columns'>
            <RecipientEdit 
              updateRecipient={this.updateRecipient}
              recipient={this.state.recipient}
              selectedRecipient={this.state.selectedRecipient}
              />
            <RecipientList recipients={this.state.recipients} removeRecipient={this.removeRecipient} selectRecipient={this.selectRecipient} />
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