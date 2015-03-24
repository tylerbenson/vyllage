var React = require('react');

var EditsCount = React.createClass({

  saveHandler: function() {
    if(this.props.saveHandler) {
      this.props.saveHandler();
    }
  },

  render: function() {
    return (
      <a href="#" className="number-buttons edits"> 
        <i className="icon ion-ios-compose-outline"></i>
        <span className="number edits"> 0 </span>
        edits
      </a>
    );
  }
});

module.exports = EditsCount;


