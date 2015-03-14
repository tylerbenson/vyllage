var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('./endpoints');
var urlTemplate = require('url-template');

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
        } 
      })
  },
  onGetSections: function () {

  },
  onGetSection: function () {

  },
  onPostSection: function () {

  },
  onPutSection: function () {

  },
  onDeleteSection: function () {

  },
  onGetComments: function () {

  },
  onPostComment: function () {

  },
  getInitialState: function () {
    return this.resume;
  },
})
