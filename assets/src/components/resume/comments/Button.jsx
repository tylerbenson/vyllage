var React = require('react');
var Actions = require('./actions');

var Comments = React.createClass({
  clickHandler: function () {
    Actions.toggleComments();
  },
  render: function () {
    return (
        <div className="one columns">
          <a className="" onClick={this.clickHandler}>comments</a>
          <span className="controls-count">{this.props.count}</span>
        </div>
    );
  }
});

module.exports = Comments;