var React = require('react');
var CommentForm = require('./Form');
var CommentList = require('./List');

var Comments = React.createClass({
  render: function () {
    var section = this.props.section || {};
    if (section.showComments) {
      return (
        <div>
          <CommentForm 
            sectionId={section.sectionId}
          />
          <CommentList 
            sectionId={section.sectionId}
            comments={section.comments}
          />
        </div>
      );
    } else {
      return null;
    }
  }
});

module.exports = Comments;