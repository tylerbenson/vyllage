var Reflux = require('reflux');
var request = require('superagent');
var template = require('url-template');
var endpoints = require('../endpoints');

var comments = [
  {commentId: 1, commentText: 'Looks good'},
  {commentId: 2, commentText: 'Improve experience section'},
  {commentId: 3, commentText: 'Fix typo in line two'},
]

module.exports = Reflux.createStore({
  listenables: require("./actions"),
  onGetComments: function (params) {
    var commentsUrl = template.parse(endpoints.resumeComments)
                              .expand(params);
    request
      .get(commentsUrl)
      .end(function (err, res) {
        if (res.status === '200') {
          this.comments = res.body;
          this.update();
        } else {
          // only for tempory use. Remove it in production
          this.comments = comments;
          this.update();
        }
      }.bind(this))
  },
  onPostComment: function () {

  },
  update: function () {
    this.trigger({
      comments: this.comments
    });
  },
  getInitialState: function () {
    this.comment = {};
    this.comments = [];
    return {
      comment: this.comment,
      comments: this.comments
    }
  }
})