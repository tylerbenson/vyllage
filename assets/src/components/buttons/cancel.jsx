var React = require('react');

var CancelBtn = React.createClass({

  cancelHandler: function() {
    if(this.props.cancelHandler) {
      this.props.cancelHandler();
    }
  },

  render: function() {
    return (
      <button className='button button-inverted cancel' onClick={this.cancelHandler}>
        Cancel
      </button>
    );
  }
});

module.exports = CancelBtn;


