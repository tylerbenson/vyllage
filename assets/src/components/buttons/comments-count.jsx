var React = require('react');

var CommentsCount = React.createClass({
  render: function() {
  	var count = this.props.count;
    return (
      <button className="flat" onClick={this.props.onClick}>
        <i className="ion-chatbox"></i>
        {count + ' comment' + (count !== 1 ? 's' : '')}
      </button>
    );
  }
});

module.exports = CommentsCount;