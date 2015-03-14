var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
var urlTemplate = require('url-template');
var findindex = require('lodash.findindex');

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    this.resume = {};
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
    var url = urlTemplate.parse(endpoints.resumeSections).expand(params);
    request
      .post(url)
      .send(data)
      .end(function (err, res) {
        this.resume.sections.push(res.body);
        this.trigger(this.resume);
      }.bind(this))
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
