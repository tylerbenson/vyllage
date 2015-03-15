var React = require('react');

var CommentList = React.createClass({
  render: function () {
    var commentNodes = this.props.comments.map(function (comment, index) {
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