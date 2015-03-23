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
    if (this.state.value) {
      actions.postComment({
        sectionId: this.props.sectionId,
        commentText: this.state.value
      });
      this.setState({value: ''});
    }
  },
  render: function () {
    return (
      <div className='row comment-form'>
        <textarea
          className='offset-by-two seven columns'
          placeholder='share your advice'
          onChange={this.changeHandler}
          value={this.state.value}></textarea>
        <button className='button button-primary' onClick={this.commentHandler}>Send</button>
      </div>
    );
  }
});

module.exports = CommentForm;