var React = require('react');
var CommentForm = require('./Form');
var CommentList = require('./List');

var Comments = React.createClass({
  render: function () {
    var section = this.props.section || {};
    if (section.showComments) {
      return (
        <div>
          <CommentList 
            sectionId={section.sectionId}
            comments={section.comments}
          />
          <CommentForm 
            sectionId={section.sectionId}
          />
        </div>
      );
    } else {
      return null;
    }
  }
});

module.exports = Comments;