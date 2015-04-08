var React = require('react');
var actions = require('../actions');
var Textarea = require('react-textarea-autosize');

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
          <Textarea
            rows='1'
            placeholder='Share your advice..'
            onChange={this.changeHandler}
            value={this.state.value}></Textarea>
          <button onClick={this.commentHandler}>Submit</button>
        </div>
      </div>
    );
  }
});

module.exports = CommentForm;