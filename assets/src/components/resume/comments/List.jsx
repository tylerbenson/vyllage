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
    var deleteStyle = {
      position:'absolute',
      right: '5px',
      marginTop : '-10px'
    }
    var user_id = document.getElementById('meta_userInfo_user') === null ? null : document.getElementById('meta_userInfo_user').content;

    var commentNodes = comments.map(function (comment, index) {
      if(!comment.commentText){
        return null;
      }
      var deleteShow = null;
      if( user_id == comment.userId || this.props.owner ){
        deleteShow =  <span className="delete-comment" onClick={this._handleDelete.bind(this,comment)}><i className="ion-trash-a"></i></span>;
      }

      return <div key={index} className='comment'>
              <div className='content'>
                <div className='avatar-container'>
                  <Avatar src={comment.avatarUrl} size="30" />
                </div>
                <div className='wrapper'>
                  {deleteShow}
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
  },
  _handleDelete:function( comment ){
    var confirmDelete =  confirm('do you really want to delete this ?');
    if( confirmDelete ){
      actions.deleteComment( comment ,this.props.sectionId);
    }
  }
});

module.exports = CommentList;