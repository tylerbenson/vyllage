var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
var urlTemplate = require('url-template');
var findindex = require('lodash.findindex');
var omit = require('lodash.omit');

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    this.tokenHeader = document.getElementById('meta_header').content,
    this.tokenValue = document.getElementById('meta_token').content;
    this.documentId = window.location.pathname.split('/')[2];
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
    var url = urlTemplate
                .parse(endpoints.resumeHeader)
                .expand({documentId: this.documentId});
    request
      .post(url)
      .set(this.tokenHeader, this.tokenValue) 
      .send({tagline: tagline})
      .end(function (err, res) {
        this.resume.header.tagline = tagline;
        this.trigger(this.resume);
      }.bind(this))            
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
          console.log(res.body);
          this.trigger(this.resume);
        } 
      }.bind(this))
  },

  onPostSection: function (data) {
    var url = urlTemplate
                .parse(endpoints.resumeSections)
                .expand({
                  documentId: this.documentId,
                });
    data.sectionPosition = this.resume.sections.length;
    request
      .post(url)
      .set(this.tokenHeader, this.tokenValue) 
      .send(data)
      .end(function (err, res) {
        this.resume.sections.push(res.body);
        this.trigger(this.resume);
      }.bind(this));
  },
  onPutSection: function (data) {
    var url = urlTemplate
                .parse(endpoints.resumeSection)
                .expand({
                  documentId: this.documentId,
                  sectionId: data.sectionId
                });
    request
      .put(url)
      .set(this.tokenHeader, this.tokenValue) 
      .send(omit(data, 'uiEditMode'))
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {sectionId: data.sectionId});
        this.resume.sections[index] = data;
        this.trigger(this.resume);
      }.bind(this));
  },
  onDeleteSection: function (sectionId) {
    var url = urlTemplate
                .parse(endpoints.resumeSection)
                .expand({
                  documentId: this.documentId,
                  sectionId: sectionId
                }); 
    request
      .del(url)
      .set(this.tokenHeader, this.tokenValue) 
      .set('Content-Type', 'application/json')
      .send({documentId: this.documentId, sectionId: sectionId})
      .end(function (err, res) {
        console.log(err, res);
        var index = findindex(this.resume.sections, {sectionId: sectionId});
        this.resume.sections.splice(index, 1);
        this.trigger(this.resume);
      }.bind(this));           
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
  onPostComment: function (data) {
    var url = urlTemplate.parse(endpoints.resumeComments).expand(data);
    request
      .post(url)
      .send(data)
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {sectionId: data.sectionId});
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
