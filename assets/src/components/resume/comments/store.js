var Reflux = require('reflux');
var request = require('superagent');
var template = require('url-template');
var endpoints = require('../../endpoints');

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
  onAddComment: function (comment) {
    this.comments = [comment].concat(this.comments);
    this.update();
  },
  onToggleComments: function () {
    this.showComments = !this.showComments;
    this.update();
  },
  getProperties: function () {
    return {
      comment: this.comment,
      comments: this.comments,
      showComments: this.showComments,
    }
  },
  update: function () {
    this.trigger(this.getProperties());
  },
  getInitialState: function () {
    this.comment = {};
    this.comments = [];
    this.showComments = false;
    return this.getProperties();
  }
})