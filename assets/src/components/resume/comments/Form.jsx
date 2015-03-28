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
      <div className='comment-box'>
        <div className='content'>
          <textarea
            rows='1'
            placeholder='share your advice'
            onChange={this.changeHandler}
            value={this.state.value}></textarea>
          <button onClick={this.commentHandler}>Submit</button>
        </div>
      </div>
    );
  }
});

module.exports = CommentForm;