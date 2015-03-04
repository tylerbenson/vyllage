var React = require('react');
var RecipientEdit = require('./RecipientEdit');
var RecipientList = require('./RecipientList');
var Suggestions = require('./RecipientSuggestions');
var assign = require('lodash.assign');

var suggestions = {
  recent: [
    {"firstName": "Tyler", "lastName": "Benson", "email": "tyler.benson@vyllage.com"},
    {"firstName": "Nathon", "lastName": "Benson", "email": "nathon.benson@vyllage.com"},
    {"firstName": "Nick", "lastName": "Disney", "email": "nick.disney@vyllage.com"},
    {"firstName": "Keith", "lastName": "Biggs", "email": "keith.biggs@vyllage.com"},
    {"firstName": "Devin", "lastName": "Moncor", "email": "devin.moncor@vyllage.com"}
  ],
  recommended: [  
    {"firstName": "Tyler", "lastName": "Benson", "email": "tyler.benson@vyllage.com"},
    {"firstName": "Nathon", "lastName": "Benson", "email": "nathon.benson@vyllage.com"},
    {"firstName": "Nick", "lastName": "Disney", "email": "nick.disney@vyllage.com"},
    {"firstName": "Keith", "lastName": "Biggs", "email": "keith.biggs@vyllage.com"},
    {"firstName": "Devin", "lastName": "Moncor", "email": "devin.moncor@vyllage.com"},
  ]
}

var FormTo = React.createClass({
  getInitialState: function () {
    return {
      suggestions: suggestions,
      recipients: [
        {"firstName": "Tyler", "lastName": "Benson", email: "tyler.benson@vyllage.com" },
        {"firstName": "Nathan", "lastName": "Benson", email: "nathan.benson@vyllage.com" }
      ],
      selectedRecipient: null,
      selectedSuggestion: null,
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
      selectedSuggestion: null,
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
  selectSuggestion: function (index) {
    var suggestions = this.state.suggestions.recent.concat(this.state.suggestions.recommended);
    this.updateRecipient(suggestions[index])
  },
  changeSelectedSuggestion: function (e) {
    var suggestions = this.state.suggestions.recent.concat(this.state.suggestions.recommended);
    var selectedSuggestion;
    if (e.key === 'ArrowDown') {
      if (this.state.selectedSuggestion === null) {
        selectedSuggestion = 0;
      } else if (this.state.selectedSuggestion < suggestions.length -1) {
        selectedSuggestion = this.state.selectedSuggestion + 1;
      } else {
        selectedSuggestion = this.state.selectedSuggestion;
      }
    }

    if (e.key === 'ArrowUp') {
      selectedSuggestion = (this.state.selectedSuggestion > 0)? this.state.selectedSuggestion - 1: 0;
    } 
    this.setState({selectedSuggestion: selectedSuggestion});
  },
  closeSuggestions: function (e) {
    e.preventDefault();
    this.setState({
      showSuggestions: false,
      selectedSuggestion: null
    });
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
          <div className="one column rqst-key-word" style={{paddingTop: '16px'}}>
            to:
          </div>
          <div className='nine columns'>
            <RecipientEdit 
              onChange={this.changeRecipient}
              onSubmit={this.updateRecipient}
              recipient={this.state.recipient}
              selectedRecipient={this.state.selectedRecipient}
              selectedSuggestion={this.state.selectedSuggestion}
              changeSelectedSuggestion={this.changeSelectedSuggestion}
              selectSuggestion={this.selectSuggestion}
              closeSuggestions={this.closeSuggestions}
              openSuggestions={this.openSuggestions} />
            <RecipientList 
              recipients={this.state.recipients}
              removeRecipient={this.removeRecipient}
              selectRecipient={this.selectRecipient} 
              selectedRecipient={this.state.selectedRecipient} />
            <Suggestions
              show={this.state.showSuggestions}
              suggestions={this.state.suggestions}
              selectSuggestion={this.selectSuggestion}
              selectedSuggestion={this.state.selectedSuggestion} />
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