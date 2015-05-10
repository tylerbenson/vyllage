var React = require('react');
var CommentForm = require('./Form');
var CommentList = require('./List');
var actions = require('../actions');

var Comments = React.createClass({
  render: function () {
    var section = this.props.section || {};
    if (section.showComments) {
      return (
        <div className='comments'>
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