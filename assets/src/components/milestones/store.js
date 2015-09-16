var React = require('react');
var Reflux = require('reflux');
var request = require('superagent');
var urlTemplate = require('url-template');
var filter = require('lodash.filter');

var endpoints = require('../endpoints');
var ResumeStore = require('../resume/store');

var MilestoneStore = Reflux.createStore({
  listenables: require('./actions'),
  init: function(){
    this.resume = {};
    this.resume.ownDocumentId = null;
    this.resume.header = [];
    this.resume.sections = [];

    this.milestones = [];
    this.isOpen = false;
    this.viewAll = false;

    this.listenTo(ResumeStore, this.onSyncMilestones);
  },
  onSyncMilestones: function(state){
    if(state.sections === this.resume.sections && state.header === this.resume.header) {
      return;
    }

    this.resume.header = state.header;
    this.resume.ownDocumentId = state.ownDocumentId;

    //If viewing own resumé don't request for the sections
    if(state.documentId === state.ownDocumentId) {
      this.resume.sections = state.sections;
      this.update();
    }
    else {
      this.getResume();
    }
  },
  update: function() {
    this.milestones = this.getMilestones();

    this.trigger({
      avatar: this.resume.header.avatarUrl,
      name: this.resume.header.firstName + " " + this.resume.header.lastName,
      milestones: this.milestones,
      isOpen: this.isOpen,
      viewAll: this.viewAll,
    });
  },
  getResume: function() {
    this.getHeader();
    this.getSections();
  },
  getHeader: function() {
    var url = urlTemplate
                .parse(endpoints.resumeHeader)
                .expand({documentId: this.resume.ownDocumentId});

    request
      .get(url)
      .set('Accept', 'application/json')
      .end(function (err, res) {
        if (res.ok) {
          this.resume.header = res.body;
          this.update();
        }
      }.bind(this));
  },
  getSections: function() {
    var url = urlTemplate
                .parse(endpoints.resumeSections)
                .expand({documentId: this.resume.ownDocumentId});
    request
      .get(url)
      .set('Accept', 'application/json')
      .end(function (err, res) {
        if (res.ok) {
          this.resume.sections = res.body;
          this.update();
        }
      }.bind(this));
  },
  getMilestones: function() {
    return [
      {
        text: 'Input your professional tagline!',
        icon: 'ion-compose',
        isDone: this.hasTagline(),
        priority: 0
      },
      {
        text: 'Input your contact number.',
        icon: 'ion-ios-telephone',
        isDone: this.hasContactNumber(),
        priority: 0
      },
      {
        text: 'Input your location.',
        icon: 'ion-map',
        isDone: this.hasAddress(),
        priority: 0
      },
      {
        text: 'Input your twitter username.',
        icon: 'ion-social-twitter',
        isDone: this.hasTwitterUrl(),
        priority: 0
      },
      {
        text: 'Give users an overview of your resumé. Add a Summary Section.',
        icon: 'ion-ios-book',
        isDone: this.hasSummarySection(),
        priority: 1
      },
      {
        text: 'Add an Education Section to your resumé.',
        icon: 'ion-bookmark',
        isDone: this.hasEducationSection(),
        priority: 2
      },
      {
        text: 'Got some job experience? Add a Job Experience Section to your resumé to showcase that.',
        icon: 'ion-briefcase',
        isDone: this.hasExperienceSection(),
        priority: 2
      },
      {
        text: 'List your professional skills by adding a Skills Section to your resumé.',
        icon: 'ion-hammer',
        isDone: this.hasSkillsSection(),
        priority: 3
      },
      {
        text: 'What are the fields that you find most interesting? Add the Career Interests Section and list them all.',
        icon: 'ion-heart',
        isDone: this.hasCareerInterestsSection(),
        priority: 3
      },
      {
        text: 'Receive a feedback. Share your resumé to your friends and mentors and get some pieces of advice.',
        icon: 'ion-chatbubble',
        isDone: this.hasComments(),
        priority: 4
      }
    ];
  },
  hasTagline: function() {
    return Boolean(this.resume.header.tagline);
  },
  hasAddress: function() {
    return Boolean(this.resume.header.address);
  },
  hasContactNumber: function() {
    return Boolean(this.resume.header.phoneNumber);
  },
  hasTwitterUrl: function() {
    return Boolean(this.resume.header.twitter);
  },
  hasSummarySection: function() {
    return filter(this.resume.sections, {type: "SummarySection"}).length > 0;
  },
  hasEducationSection: function() {
    return filter(this.resume.sections, {type: "EducationSection"}).length > 0;
  },
  hasExperienceSection: function() {
    return filter(this.resume.sections, {type: "JobExperienceSection"}).length > 0;
  },
  hasSkillsSection: function() {
    return filter(this.resume.sections, {type: "SkillsSection"}).length > 0;
  },
  hasCareerInterestsSection: function() {
    return filter(this.resume.sections, {type: "CareerInterestsSection"}).length > 0;
  },
  hasComments: function() {
    return filter(this.resume.sections, function(section){
      return section.numberOfComments > 0;
    }).length > 0;
  },
  onToggle: function(flag) {
    this.isOpen = flag !== undefined ? flag : !this.isOpen;
    this.update();
  },
  onViewAllToggle: function(flag) {
    this.viewAll = flag !== undefined ? flag : !this.viewAll;
    this.update();
  }
});

module.exports = MilestoneStore;