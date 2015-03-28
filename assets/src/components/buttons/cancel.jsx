var React = require('react');

var CancelBtn = React.createClass({

  cancelHandler: function() {
    if(this.props.cancelHandler) {
      this.props.cancelHandler();
    }
  },

  render: function() {
    return (
      <button className='small secondary inverted' onClick={this.cancelHandler}>
        Cancel
      </button>
    );
  }
});

module.exports = CancelBtn;


