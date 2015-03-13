var React = require('react');
var Reflux = require('reflux');
var CommentsButton = require('./Button');
var CommmentsStore = require('./store');
var CommentsForm = require('./Form');
var CommentsList = require('./List');
var Actions = require('./actions');

var Controls = React.createClass({
  mixins: [Reflux.connect(CommmentsStore)],
  componentDidMount: function () {
    Actions.getComments(this.props);
  },
  render: function() {
    return (
      <div className="controls">
        <div className="row">
          <div className="offset-by-eight two columns">
            <a className="">suggestions</a>
            <span className="controls-count">2</span>
          </div>
          <CommentsButton count={this.state.comments.length}/>
        </div>
        {this.state.showComments? <CommentsForm />: null}
        {this.state.showComments? <CommentsList comments={this.state.comments} />: null}
      </div>
    );
  }
});

module.exports = Controls;