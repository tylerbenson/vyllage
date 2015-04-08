var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
var urlTemplate = require('url-template');
var findindex = require('lodash.findindex');
var omit = require('lodash.omit');
var assign = require('lodash.assign');

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    this.tokenHeader = document.getElementById('meta_header').content,
    this.tokenValue = document.getElementById('meta_token').content;
    this.documentId = window.location.pathname.split('/')[2];
    this.resume = {
      documentId: this.documentId,
      header: {
        firstName: '',
        middleName: '',
        lastName: ''
      },
      sections: []
    }; 
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
      .put(url)
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
        var section = res.body;
        section.uiEditMode = true;
        this.resume.sections.push(section);
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
      .send(omit(data, ['uiEditMode', 'showComments', 'comments']))
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
        var index = findindex(this.resume.sections, {sectionId: sectionId});
        this.resume.sections.splice(index, 1);
        this.trigger(this.resume);
      }.bind(this));           
  },
  onGetComments: function (sectionId) {
    var url = urlTemplate
                .parse(endpoints.resumeComments)
                .expand({
                  documentId: this.documentId,
                  sectionId: sectionId
                });
    request
      .get(url)
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {sectionId: sectionId});
        this.resume.sections[index].comments = res.body;
        this.trigger(this.resume);
      }.bind(this))
  },
  onPostComment: function (data) {
    var url = urlTemplate
                .parse(endpoints.resumeComments)
                .expand({
                  documentId: this.documentId,
                  sectionId: data.sectionId
                });
    data = assign({}, data, {
      sectionVersion: 1
    })
    request
      .post(url)
      .set(this.tokenHeader, this.tokenValue)
      .send(data)
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {sectionId: data.sectionId});
        this.resume.sections[index].comments.push(res.body);
        this.resume.sections[index].numberOfComments += 1;
        this.trigger(this.resume);
      }.bind(this))
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
  onShowComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = true;
    this.trigger(this.resume);
  },
  onHideComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = false;
    this.trigger(this.resume);
  },
  onToggleComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = !this.resume.sections[index].showComments;
    this.trigger(this.resume);
  },
  getInitialState: function () {
    return this.resume;
  },
})
