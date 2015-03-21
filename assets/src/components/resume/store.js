var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
var urlTemplate = require('url-template');
var findindex = require('lodash.findindex');

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    this.documentId = window.location.pathname.split('/')[2]
    this.resume = {}; 
  },
  onGetResume: function () {
    this.onGetHeader();
    this.onGetSections();
  },
  onGetHeader: function () {
    var url = urlTemplate
                .parse(endpoints.resumeHeader)
                .expand({documentId: this.documentId});
    console.log(url);
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
  onGetSections: function () {
    var url = urlTemplate
                .parse(endpoints.resumeSections)
                .expand({documentId: this.documentId});
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
