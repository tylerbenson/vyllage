var Reflux = require('reflux');
var assign = require('lodash.assign');
var request = require('superagent');
var urlTemplate = require('url-template');
var endpoints = require('../endpoints');
var firstName;
var headerContainer = document.getElementById('header-container');
if (headerContainer) {
 firstName = headerContainer.getAttribute('name');
}

var GetFeedbackStore = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    var metaHeader = document.getElementById('meta_header');
    if (metaHeader) {
      this.tokenHeader = metaHeader.content;
    }
    var metaToken = document.getElementById('meta_token');
    if (metaToken) {
      this.tokenValue = metaToken.content;
    }
    this.recepientsError = false;
    this.notRegisteredUsers = [];
    this.processing= false;
    this.users = [];
    this.suggestions = {};
    this.selectedSuggestion = null;
    this.showSuggestions = false;
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: ""};
    this.subject = "Could you provide me some feedback on my resume?";
    this.message = "I could really use your assistance on giving me some career or resume advice. Do you think you could take a couple of minutes and look over this for me?\n\nThanks,\n" + firstName;
    this.shareableLink = null;
    this.inviteType = 'form';
    this.recommendations = [];
  },
  onSetInviteType: function(type){
    this.inviteType = type;
    this.update();
  },
  onGetShareableLink: function(){
    request
      .post(endpoints.getFeedbackGenerateLink)
      .set(this.tokenHeader, this.tokenValue)
      .send({
        'documentId': null,
        'documentType': 'resume'
      })
      .end(function(err, res){
        if (res.status === 200) {
          this.shareableLink = res.text;
          this.update();
        }
        else {
          this.shareableLink = null;
          this.update();
        }
      }.bind(this));
  },
  getRecipientUsersList: function () {
    return this.users.map(function (recipient) {
      return recipient.userId;
    })
  },
  onPostFeedback: function () {
    if (this.users.length > 0 || this.notRegisteredUsers.length > 0) {
      this.processing = true;
      this.update();
      request
        .post(endpoints.getFeedback)
        .set(this.tokenHeader, this.tokenValue)
        .send({
          csrftoken: this.tokenValue,
          users: this.users,
          notRegisteredUsers: this.notRegisteredUsers,
          subject: this.subject,
          message: this.message
        })
        .end(function (err, res) {
          if (res.status === 200) {
            window.location = '/resume'
            this.processing = false;
            this.recepientsError= false;
            this.update();
          }
        }.bind(this))
    } else {
      this.recepientsError= true;
      this.update();
    }
  },
  onGetSuggestions: function () {
    request
      .get(endpoints.getFeedbackSuggestions)
      .set('Accept', 'application/json')
      .query({firstNameFilter: this.recipient.firstName})
      .query({lastNameFilter: this.recipient.lastName})
      .query({emailFilter: this.recipient.email})
      .end(function (err, res) {
        if (res.ok) {
          var alreadyAddedRecipents = this.getRecipientUsersList();
          var suggestions = {};
          suggestions.recent = [];
          suggestions.recommended = [];
          res.body.recent.forEach(function (suggestion) {
            if (alreadyAddedRecipents.indexOf(suggestion.userId) === -1) {
              suggestions.recent.push(suggestion);
            }
          });
          res.body.recommended.forEach(function (suggestion) {
            if (alreadyAddedRecipents.indexOf(suggestion.userId) === -1) {
              suggestions.recommended.push(suggestion);
            }
          });
          this.suggestions = suggestions;
          this.update();
        } else {
          this.suggestions = {};
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
    this.notRegisteredUsers.push(recipient)
    this.selectedRecipient = null;
    this.recepientsError = false;
    this.recipient = {firstName: "", lastName: "", email: ""};
    this.showSuggestions = false;
    this.update();
  },
  onUpdateRecipient: function (recipient, index) {
    this.notRegisteredUsers[index] = recipient;
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: ""};
    this.showSuggestions = false;
    this.update();
  },
  onRemoveUserRecipient: function (index) {
    this.users.splice(index, 1);
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: ""};
    this.showSuggestions = false;
    this.update();
  },
  onRemoveNotRegisteredUserRecipient: function (index) {
    this.notRegisteredUsers.splice(index, 1);
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: ""};
    this.showSuggestions = false;
    this.update();
  },
  onSelectRecipient: function (index) {
    this.selectedRecipient = index;
    this.recipient = assign({}, this.notRegisteredUsers[index]);
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
    this.users.push(this.suggestions.recent[index]);
    this.selectedRecipient = null;
    this.recepientsError = false;
    this.recipient = {firstName: "", lastName: "", email: ""};
    this.update();
  },
  onSelectRecommendedSuggestion: function (index) {
    this.users.push(this.suggestions.recommended[index]);
    this.selectedRecipient = null;
    this.recepientsError = false;
    this.recipient = {firstName: "", lastName: "", email: ""};
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
  onGetRecommendations: function(){
    request
      .get(endpoints.getFeedbackSuggestions)
      .set('Accept', 'application/json')
      .end(function (err, res) {
        if(res.ok) {
          this.recommendations = res.body.recommended;
        }
        else {
          this.recommendations = [];
        }
        this.update();
      }.bind(this));
    // var response = [{
    //   id: 1,
    //   name: 'David Greene',
    //   tagline: 'Helping People Achieve Greater Careers',
    //   avatar: '/images/avatars/1.jpg',
    //   is_sponsored: true
    // },
    // {
    //   id: 2,
    //   name: 'Stefanie Reyes',
    //   tagline: 'Making Change through Strong Leadership',
    //   avatar: '/images/avatars/2.jpg',
    //   is_sponsored: false
    // },
    // {
    //   id: 3,
    //   name: 'John Lee',
    //   tagline: 'Aspiring Project Management Technologist',
    //   avatar: '/images/avatars/3.jpg',
    //   is_sponsored: false
    // },
    // {
    //   id: 4,
    //   name: 'Jessica Knight',
    //   tagline: 'Executive Team Lead',
    //   avatar: '/images/avatars/4.jpg',
    //   is_sponsored: false
    // },
    // {
    //   id: 5,
    //   name: 'Carl Jensen',
    //   tagline: 'Success through Sales',
    //   avatar: '/images/avatars/5.jpg',
    //   is_sponsored: true
    // }];
  },
  onRequestForFeedback: function(index){
    var invited_user = this.recommendations[index];
    //Request here
    request
      .post(endpoints.getFeedback)
      .set(this.tokenHeader, this.tokenValue)
      .send({
        csrftoken: this.tokenValue,
        users: [this.recommendations[index]],
        notRegisteredUsers: [],
        subject: this.subject,
        message: this.message
      })
      .end(function (err, res) {
        if (res.status === 200) {
          //Remove from recommendations
          this.recommendations.splice(index, 1);
          this.update();
        }
      }.bind(this));
  },
  update: function () {
    this.trigger({
      users: this.users,
      notRegisteredUsers: this.notRegisteredUsers,
      suggestions: this.suggestions,
      selectedSuggestion: this.selectedSuggestion,
      showSuggestions: this.showSuggestions,
      selectedRecipient: this.selectedRecipient,
      recipient: this.recipient,
      subject: this.subject,
      message: this.message,
      processing: this.processing,
      shareableLink: this.shareableLink,
      inviteType: this.inviteType,
      recommendations: this.recommendations
    });
  },
  getInitialState: function () {

    return {
      users: this.users,
      notRegisteredUsers: this.notRegisteredUsers,
      suggestions: this.suggestions,
      selectedSuggestion: this.selectedSuggestion,
      showSuggestions: this.showSuggestions,
      selectedRecipient: this.selectedRecipient,
      recipient: this.recipient,
      subject: this.subject,
      message: this.message,
      processing: this.processing,
      shareableLink: this.shareableLink,
      inviteType: this.inviteType,
      recommendations: this.recommendations
    }
  }
});

module.exports = GetFeedbackStore;