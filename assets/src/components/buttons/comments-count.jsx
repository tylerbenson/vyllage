var React = require('react');

var CommentsCount = React.createClass({

  saveHandler: function() {
    if(this.props.saveHandler) {
      this.props.saveHandler();
    }
  },

  render: function() {
    return (
      <a href="#" className="number-buttons comments">
        <i className="icon ion-chatbox"></i>
        <span className="number comments"> 0 </span>
        comments
      </a>
    );
  }
});

module.exports = CommentsCount;