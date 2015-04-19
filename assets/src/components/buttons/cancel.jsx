var React = require('react');

var CancelBtn = React.createClass({
  render: function() {
    return (
      <button tabIndex={-1} className='cancel small secondary inverted' onClick={this.props.onClick}>
        <i className="ion-close"></i>
        <span>Cancel</span>
      </button>
    );
  }
});

module.exports = CancelBtn;


