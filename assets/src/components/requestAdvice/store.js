var Reflux = require('reflux');
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
};

var recipients = [
  {"firstName": "Tyler", "lastName": "Benson", email: "tyler.benson@vyllage.com" },
  {"firstName": "Nathan", "lastName": "Benson", email: "nathan.benson@vyllage.com" }
];

var RequestAdviceStore = Reflux.createStore({
  listenables: require('./actions'),
  onChangeRecipient: function (key, value) {
    this.recipient[key] = value;
    this.update();
  },
  onAddRecipient: function (recipient) {
    this.recipients.push(recipient)
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: "", newRecipient: true};
    this.showSuggestions = false;
    this.update();
  },
  onUpdateRecipient: function (recipient, index) {
    this.recipients[index] = recipient;
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: "", newRecipient: true};
    this.showSuggestions = false;
    this.update();
  },
  onRemoveRecipient: function (index) {
    this.recipients.splice(index, 1);
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: "", newRecipient: true};
    this.showSuggestions = false;
    this.update();
  },
  selectRecipient: function (index) {
    var recipient = assign({}, this.recipients[index]);
    if (recipient.newRecipient) {
      this.selectedRecipient = index;
      this.recipient = recipient;
    } else {
      this.selectedRecipient = null;
      this.recipient = {firstName: "", lastName: "", email: "", newRecipient: true};
    }
    this.update();
  },
  onSuggestionIndex: function (index) {
    this.selectedSuggestion = index;
    this.update();
  },
  onSelectSuggestion: function (index) {
    if (index < this.suggestions.recent.length) {
      this.onSelectRecentSuggestion(index);
    } else {
      this.onSelectRecommendedSuggestion(index - this.suggestions.recent.length);
    }
    this.update();
  },
  onSelectRecentSuggestion: function (index) {
    this.recipients.push(this.suggestions.recent[index]);
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: "", newRecipient: true};
    this.update();
  },
  onSelectRecommendedSuggestion: function (index) {
    this.recipients.push(this.suggestions.recommended[index]);
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: "", newRecipient: true};
    this.update();
  },
  openSuggestions: function () {
    this.showSuggestions = (this.selectedRecipient === null);
    this.update();
  },
  closeSuggestions: function () {
    this.showSuggestions = false
    this.selectedRecipient = null
    this.update();
  },
  update: function () {
    this.trigger({
      recipients: this.recipients,
      suggestions: this.suggestions,
      selectedSuggestion: this.selectedSuggestion,
      showSuggestions: this.showSuggestions,
      selectedRecipient: this.selectedRecipient,
      recipient: this.recipient,
    })
  },
  getInitialState: function () {
    this.recipients = recipients;
    this.suggestions = suggestions;
    this.selectedSuggestion = null;
    this.showSuggestions = false;
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: "", newRecipient: true};
    return {
      recipients: this.recipients,
      suggestions: this.suggestions,
      selectedSuggestion: this.selectedSuggestion,
      showSuggestions: this.showSuggestions,
      selectedRecipient: this.selectedRecipient,
      recipient: this.recipient,
    }
  }
});

module.exports = RequestAdviceStore;