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
  sections: [
    {
      "type": "career-goal",
      "title": "career goal",
      "sectionPosition": 1,
      "state": "shown",
      "description": ""
    },
    {
      "type": "experience",
      "title": "experience",
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
    // this.resume = {}; // uncomment of api call integration
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
    this.resume.sections.push(data);
    this.trigger(this.resume);
  },
  onPutSection: function (data, params) {
    var url = urlTemplate.parse(endpoints.resumeSection).expand(params);
    request
      .post(url)
      .send(data)
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {documentId: params.documentId});
        this.resume.sections[index] = res.body;
        this.trigger(this.resume);
      })
  },
  onDeleteSection: function (params) {
    var url = urlTemplate.parse(endpoints.resumeSection).expand(params);
    request
      .delete(url)
      .end(function (err, res) {
      });
  },
  onGetComments: function (params) {
    var url = urlTemplate.parse(endpoints.resumeComments).expand(params);
    request
      .get(url)
      .end(function (err, res) {
        if (res.ok) {
          var index = findindex(this.resume.sections, {documentId: params.documentId});
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
        var index = findindex(this.resume.sections, {documentId: params.documentId});
        this.resume.sections[index].comments.unshift(res.body);
      })
  },
  getInitialState: function () {
    return this.resume;
  },
})
