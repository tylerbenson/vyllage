var React = require('react');
var actions = require('../actions');
var moment = require('moment');
var Avatar = require('../../avatar');

var CommentList = React.createClass({
  // componentWillMount: function () {
  //   actions.getComments(this.props.sectionId);
  // },
  render: function () {
    var comments = this.props.comments || [];
    var commentNodes = comments.map(function (comment, index) {
      if(comment.commentText.length<1){
        return null;
      }
      return <div key={index} className='comment'>
              <div className='content'>
                <div className='avatar-container'>
                  <Avatar src={comment.avatarUrl} size="30" />
                </div>
                <div className='wrapper'>
                  <div className='info'>
                    <div className="author">{comment.userName?comment.userName:'Vyllage User'}</div>
                    <div className="timestamp">
                      {moment(comment.lastModified).isValid() ? moment.utc(comment.lastModified).fromNow(): ''}
                    </div>
                  </div>
                  <div className="message">
                    {comment.commentText.replace(/\n{3,}/,'\n\n')}
                  </div>
                </div>
              </div>
             </div>

    }.bind(this));
    return (
      <div>
        {commentNodes}
      </div>
    );
  }
});

module.exports = CommentList;