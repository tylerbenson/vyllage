var React = require('react');
var Reflux = require('reflux');
var CommmentsStore = require('./store');
var Actions = require('./actions');

var Comments = React.createClass({
  mixins: [Reflux.connect(CommmentsStore)],
  componentDidMount: function () {
    Actions.getComments(this.props);
  },
  render: function () {
    return (
        <div className=" u-pull-left">
          <a className="comments" onClick={this.clickHandler}>comments</a>
          <span className="suggestions-count count">{this.state.comments.length}</span>
        </div>
    );
  }
});

module.exports = Comments;