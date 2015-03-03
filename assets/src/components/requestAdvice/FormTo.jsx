var React = require('react');
var RecipientEdit = require('./RecipientEdit');
var RecipientList = require('./RecipientList');
var Suggestions = require('./RecipientSuggestions');
var assign = require('lodash.assign');

var FormTo = React.createClass({
  getInitialState: function () {
    return {
      recipients: [
        {"firstName": "Tyler", "lastName": "Benson", email: "tyler.benson@vyllage.com" },
        {"firstName": "Nathan", "lastName": "Benson", email: "nathan.benson@vyllage.com" }
      ],
      selectedRecipient: null,
      recipient: {firstName: "", lastName: "", email: "", newRecipient: true},
      showSuggestions: false
    };
  },
  changeRecipient: function (key, value) {
    var recipient = this.state.recipient;
    if (recipient.newRecipient) {
      recipient[key] = value
    }
    this.setState({
      recipient: recipient,
      showSuggestions: (this.state.selectedRecipient === null)
    });
  },
  updateRecipient: function (recipient) {
    var recipients = this.state.recipients;
    if (this.state.selectedRecipient === null) {
      recipients = recipients.concat(recipient);
    } else {
      recipients[this.state.selectedRecipient] = recipient; 
    }
    this.setState({
      showSuggestions: false,
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
      recipient: assign({}, this.state.recipients[index])
    });
  },
  closeSuggestions: function (e) {
    e.preventDefault();
    this.setState({showSuggestions: false});
  },
  openSuggestions: function (e) {
    e.preventDefault();
    var recipient = this.state.recipient;
    if (recipient.firstName || recipient.lastName || recipient.email) {
      this.setState({showSuggestions: this.state.selectedRecipient === null});
    }
  },
  render: function () {
    return (
        <div className='request-advice-form-to row'>
          <span className="one column rqst-key-word">to:</span>
          <div className='nine columns'>
            <RecipientEdit 
              onChange={this.changeRecipient}
              onSubmit={this.updateRecipient}
              recipient={this.state.recipient}
              closeSuggestions={this.closeSuggestions}
              openSuggestions={this.openSuggestions} />
            <RecipientList 
              recipients={this.state.recipients}
              removeRecipient={this.removeRecipient}
              selectRecipient={this.selectRecipient} 
              selectedRecipient={this.state.selectedRecipient} />
            <Suggestions show={this.state.showSuggestions} selectSuggestion={this.updateRecipient} />
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