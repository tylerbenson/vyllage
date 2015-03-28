var React = require('react');

var CommentsCount = React.createClass({
  render: function() {
    return (
      <button className="flat" onClick={this.props.onClick}>
        <i className="ion-chatbox"></i>
        {this.props.count} comments
      </button>
    );
  }
});

module.exports = CommentsCount;