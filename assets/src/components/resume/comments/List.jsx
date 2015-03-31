var React = require('react');
var actions = require('../actions');

var CommentList = React.createClass({
  componentDidMount: function () {
    actions.getComments(this.props.sectionId);
  },
  render: function () {
    var comments = this.props.comments || [];
    var commentNodes = comments.map(function (comment, index) {
      return <div key={index} className='comment'>
              <div className='content'>
                <div className='info'>
                  <div className="author">{comment.userName}</div>
                  <div className="timestamp"></div>
                </div>  
                <div className="message">
                  {comment.commentText}
                </div>
              </div>
             </div>           

    });
    return (
      <div>
        {commentNodes}
      </div>
    );
  }
});

module.exports = CommentList;