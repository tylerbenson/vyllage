var Reflux = require('reflux');
var assign = require('lodash.assign');
var uniq = require('lodash.uniq');
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
    this.suggestionPosition ="left";
    this.selectedRecipient = null;
    this.recipient = {firstName: "", lastName: "", email: ""};
    this.subject = "Could you provide me some feedback on my resume?";
    this.message = "I could really use your assistance on giving me some career or resume advice. Do you think you could take a couple of minutes and look over this for me?\n\nThanks,\n" + firstName;
    this.shareableLink = null;
    this.inviteType = 'form';
    this.recommendations = [];
    this.useDummyData = false;
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
  onOpenSuggestions: function (key) {
    if( key == 'lastName'){
      this.suggestionPosition = "right";
    }else{
      this.suggestionPosition = "left";
    }
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
    var url = urlTemplate
      .parse(endpoints.togglz)
      .expand({feature: 'DUMMY_SUGGESTIONS'});

    request
      .get(url)
      .end(function(err, res) {
        this.useDummyData = res.body;
        this.update();

        if(this.useDummyData) {
          this.recommendations = [{
            userId: 1,
            firstName: 'David',
            lastName: 'Greene',
            tagline: 'Helping People Achieve Greater Careers',
            avatarUrl: '/images/avatars/1.jpg',
            advisor: true
          },
          {
            userId: 2,
            firstName: 'Stefanie',
            lastName: 'Reyes',
            tagline: 'Making Change through Strong Leadership',
            avatarUrl: '/images/avatars/2.jpg',
            advisor: false
          },
          {
            userId: 3,
            firstName: 'John',
            lastName: 'Lee',
            tagline: 'Aspiring Project Management Technologist',
            avatarUrl: '/images/avatars/3.jpg',
            advisor: false
          },
          {
            userId: 4,
            firstName: 'Jessica',
            lastName: 'Knight',
            tagline: 'Executive Team Lead',
            avatarUrl: '/images/avatars/4.jpg',
            advisor: false
          },
          {
            userId: 5,
            firstName: 'Carl',
            lastName: 'Jensen',
            tagline: 'Success through Sales',
            avatarUrl: '/images/avatars/5.jpg',
            advisor: true
          }];
          this.update();
        }
        else {
          request
            .get(endpoints.getFeedbackSuggestions)
            .set('Accept', 'application/json')
            .end(function (err, res) {
              if(res.ok) {
                this.recommendations = uniq(res.body.recommended.concat(res.body.recent), 'userId');
              }
              else {
                this.recommendations = [];
              }
              this.update();
            }.bind(this));
        }
      }.bind(this));
  },
  onRequestForFeedback: function(index){
    var recommendation = this.recommendations[index];

    var invited_user = {
      userId: recommendation.userId,
      email: recommendation.email,
      firstName: recommendation.firstName,
      middleName: recommendation.middleName,
      lastName: recommendation.lastName,
    };

    if(!this.useDummyData){
    request
      .post(endpoints.getFeedback)
      .set(this.tokenHeader, this.tokenValue)
      .send({
        users: [invited_user],
        notRegisteredUsers: [],
        subject: this.subject,
        message: this.message,
        "allowGuestComments": true
      })
      .end(function (err, res) {
        if (res.status === 200) {
          //Remove from recommendations
          this.recommendations.splice(index, 1);
          this.update();
        }
      }.bind(this));
    }
    else {
      //Remove from recommendations
      this.recommendations.splice(index, 1);
      this.update();
    }
  },
  update: function () {
    this.trigger({
      users: this.users,
      notRegisteredUsers: this.notRegisteredUsers,
      suggestions: this.suggestions,
      selectedSuggestion: this.selectedSuggestion,
      showSuggestions: this.showSuggestions,
      suggestionPosition : this.suggestionPosition,
      selectedRecipient: this.selectedRecipient,
      recipient: this.recipient,
      subject: this.subject,
      message: this.message,
      processing: this.processing,
      shareableLink: this.shareableLink,
      inviteType: this.inviteType,
      recommendations: this.recommendations,
      useDummyData: this.useDummyData
    });
  },
  getInitialState: function () {

    return {
      users: this.users,
      notRegisteredUsers: this.notRegisteredUsers,
      suggestions: this.suggestions,
      selectedSuggestion: this.selectedSuggestion,
      showSuggestions: this.showSuggestions,
      suggestionPosition : this.suggestionPosition,
      selectedRecipient: this.selectedRecipient,
      recipient: this.recipient,
      subject: this.subject,
      message: this.message,
      processing: this.processing,
      shareableLink: this.shareableLink,
      inviteType: this.inviteType,
      recommendations: this.recommendations,
      useDummyData: this.useDummyData
    }
  }
});

module.exports = GetFeedbackStore;
