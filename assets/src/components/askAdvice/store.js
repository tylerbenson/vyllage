var Reflux = require('reflux');
var assign = require('lodash.assign');
var request = require('superagent');
var urlTemplate = require('url-template');
var endpoints = require('../endpoints');
 
var AskAdviceStore = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    this.tokenHeader = document.getElementById('meta_header').content,
    this.tokenValue = document.getElementById('meta_token').content;
    this.documentId = window.location.pathname.split('/')[2];
    this.recipients = [];
    this.suggestions = {};
    this.selectedSuggestion = null;
    this.showSuggestions = false;
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: "", newRecipient: true};
    this.subject = '';
    this.message = '';
  },
  onPostAskAdvice: function () {
    var url = urlTemplate
                .parse(endpoints.askAdvice)
                .expand({documentId: this.documentId});
    request
      .post(url)
      .set(this.tokenHeader, this.tokenValue) 
      .send({
        to: this.recipients,
        subject: this.subject,
        message: this.message
      })
      .end(function (err, res) {
        if (res.status === 200) {
          // Update the actual redirect location for production
          window.location = '/resume'
        }
      })
  }, 
  onGetSuggestions: function () {
    var url = urlTemplate
                .parse(endpoints.askAdviceSuggestions)
                .expand({documentId: this.documentId});
    request
      .get(url)
      .query({firstNameFilter: this.recipient.firstName})
      .query({lastNameFilter: this.recipient.lastName})
      .query({emailFilter: this.recipient.email})
      .end(function (err, res) {
        if (res.ok) {
          this.suggestions = res.body || [];
          this.update();
        } else {
          this.suggestions = [];
          this.update();
        }
      }.bind(this))
  },
  onChangeRecipient: function (key, value) {
    this.recipient[key] = value;
    this.onGetSuggestions();
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
  onSelectRecipient: function (index) {
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
  onOpenSuggestions: function () {
    this.showSuggestions = (this.selectedRecipient === null);
    this.update();
  },
  onCloseSuggestions: function () {
    this.showSuggestions = false;
    this.update();
  },
  onUpdateSubject: function (subject) {
    this.subject = subject;
    this.update();
  },
  onUpdateMessage: function (message) {
    this.message = message;
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
      subject: this.subject,
      message: this.message
    })
  },
  getInitialState: function () {
    
    return {
      recipients: this.recipients,
      suggestions: this.suggestions,
      selectedSuggestion: this.selectedSuggestion,
      showSuggestions: this.showSuggestions,
      selectedRecipient: this.selectedRecipient,
      recipient: this.recipient,
      subject: this.subject,
      message: this.message,
    }
  }
});

module.exports = AskAdviceStore;