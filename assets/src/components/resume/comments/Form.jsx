var React = require('react');
var actions = require('../actions');

var CommentForm = React.createClass({
  getInitialState: function () {
    return {value: ''}
  },
  changeHandler: function (e) {
    e.preventDefault();
    this.setState({value: e.target.value});
  },
  commentHandler: function (e) {
    e.preventDefault();
    actions.postComment({
      sectionId: this.props.sectionId,
      commentText: this.state.value
    });
    this.setState({value: ''});
  },
  render: function () {
    return (
      <div className='row comments-form'>
        <textarea className='twelve columns' onChange={this.changeHandler} value={this.state.value}></textarea>
        <button className='save-btn u-pull-right' onClick={this.commentHandler}>comment</button>
      </div>
    );
  }
});

module.exports = CommentForm;