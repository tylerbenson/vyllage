var React = require('react');
var actions = require('../actions');

var CommentList = React.createClass({
  componentDidMount: function () {
    actions.getComments(this.props.sectionId);
  },
  render: function () {
    var comments = this.props.comments || [];
    var commentNodes = comments.map(function (comment, index) {
      return <div key={index} className='row comment'>
              <div className='offset-by-two seven columns'>
                <div className="comment-name">Richard Zielke</div>
                <div className="comment-text">
                  {comment.commentText}
                </div>
              </div>
             </div>           

    });
    return (
      <div className='comment-list-block'>
        {commentNodes}
      </div>
    );
  }
});

module.exports = CommentList;