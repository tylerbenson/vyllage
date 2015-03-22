var React = require('react');
var actions = require('../comments');

var CommentList = React.createClass({
  componentDidMount: function () {
    actions.getComments(this.props.sectionId);
  },
  componentWillReceiveProps: function (nextProps) {
    actions.getComments(nextProps.sectionId);
  },
  render: function () {
    var comments = this.props.comments || [];
    var commentNodes = comments.map(function (comment, index) {
      return <div key={index} className='comment'>
                <div className="row">
                    <div className="comment-name">Richard Zielke</div>
                </div>
                <div className="row">
                  <img className="one column" src="images/comment-person.png" />
                  <div className="eleven columns comment-text">
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