var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
var urlTemplate = require('url-template');
var findindex = require('lodash.findindex');

var resume = {
  header: {
    firstName: "Nathan",
    middleName: "M",
    lastName: "Benson",
    tagline: "Technology Enthusiast analyzing, building, and expanding solutions"
  },
  contact: {
    "social": {
      "twitter":"@nben888",
      "facebook":"natebenson",
      "linkedin":"www.linkedin.com/natebenson",
      "visibility":"private"
    },
    "contact": {
      "email":"nathan@vyllage.com",
      "home":"555-890-2345",
      "cell":"555-123-2345",
      "visibility":"private"
    },
    "location": {
      "values":[
        "1906 NE 151st Cicle, Vancouver WA g8589",
        "1906 NE 151st Cicle, Vancouver WA g8589"
      ],
      "visibility":"private"
    }
  },
  sections: [
    {
      "type": "career-goal",
      "title": "Career Goal",
      "sectionId": 1,
      "sectionPosition": 1,
      "state": "shown",
      "description": "I am a frevid promoter of creating solutions"
    },
    {
      "type": "experience",
      "title": "Experience",
      "sectionId": 2,
      "sectionPosition": 2,
      "state": "shown",
      "organizationName": "",
      "organizationDescription": "",
      "role": "",
      "startDate": "",
      "endDate": "",
      "isCurrent": false,
      "location": "",
      "roleDescription": "",
      "highlights": ""
    }
  ]
}

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    // temporarily asseigned placeholder 
    this.resume = resume;
    // this.resume = {}; // uncomment after api call integration
  },
  onGetResume: function (params) {
    var url = urlTemplate.parse(endpoints.resume).expand(params);
    request
      .get(url)
      .end(function (err, res) {
        if (res.ok) {
          this.resume = res.body;
          this.trigger(this.resume);
        } 
      }.bind(this))
  },
  onGetHeader: function (params) {
    var url = urlTemplate.parse(endpoints.resumeHeader).expand(params);
    request
      .get(url)
      .end(function (err, res) {
        if (res.ok) {
          this.resume.header = res.body;
          this.trigger(this.resume);
        } 
      }.bind(this))
  },
  onUpdateTagline: function (tagline) {
    // implement api call also here
    this.resume.header.tagline = tagline;
    this.trigger(this.resume);
  },
  onGetSections: function (params) {
    var url = urlTemplate.parse(endpoints.resumeSections).expand(params);
    request
      .get(url)
      .end(function (err, res) {
        if (res.ok) {
          this.resume.sections = res.body;
          this.trigger(this.resume);
        } 
      }.bind(this))
  },
  onPostSection: function (data, params) {
    // var tokenHeader = document.getElementById('meta_header').content,
    // var tokenValue = document.getElementById('meta_token').content;
    // var url = urlTemplate.parse(endpoints.resumeSections).expand(params);
    // request
    //   .post(url)
    //   .set(tokenHeader, tokenValue) 
    //   .send(data)
    //   .end(function (err, res) {
    //     this.resume.sections.push(res.body);
    //     this.trigger(this.resume);
    //   }.bind(this))
    
    // for temporary use
    data.sectionId = this.resume.sections.length + 1;
    this.resume.sections.push(data);
    this.trigger(this.resume);
  },
  onPutSection: function (data, params) {
    // var url = urlTemplate.parse(endpoints.resumeSection).expand(params);
    // request
    //   .post(url)
    //   .send(data)
    //   .end(function (err, res) {
    //     var index = findindex(this.resume.sections, {sectionId: params.sectionId});
    //     this.resume.sections[index] = res.body;
    //     this.trigger(this.resume);
    //   });
    var index = findindex(this.resume.sections, {sectionId: data.sectionId});
    this.resume.sections[index] = data;
    this.trigger(this.resume);
  },
  onDeleteSection: function (params) {
    var index = findindex(this.resume.sections, {sectionId: params.sectionId});
    this.resume.sections.splice(index, 1);
    this.trigger(this.resume);
  },
  onGetComments: function (params) {
    var url = urlTemplate.parse(endpoints.resumeComments).expand(params);
    request
      .get(url)
      .end(function (err, res) {
        if (res.ok) {
          var index = findindex(this.resume.sections, {sectionId: params.sectionId});
          this.resume.sections[index].comments = res.body;
        } 
      }.bind(this))
  },
  onPostComment: function (params) {
    var url = urlTemplate.parse(endpoints.resumeComments).expand(params);
    request
      .post(url)
      .send(data)
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {sectionId: params.sectionId});
        this.resume.sections[index].comments.unshift(res.body);
      })
  },
  onEnableEditMode: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].uiEditMode = true;
    this.trigger(this.resume);
  },
  onDisableEditMode: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].uiEditMode = false;
    this.trigger(this.resume);
  },
  getInitialState: function () {
    return this.resume;
  },
})
