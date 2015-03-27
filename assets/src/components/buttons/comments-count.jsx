var React = require('react');

var CommentsCount = React.createClass({
  render: function() {
    return (
      <a className="number-buttons comments">
        <i className="icon ion-chatbox"></i>
        <span className="number comments"> {this.props.count} </span>
        comments
      </a>
    );
  }
});

module.exports = CommentsCount;