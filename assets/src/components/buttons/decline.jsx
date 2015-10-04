var React = require('react');

var DeclineBtn = React.createClass({
  render: function() {
    return (
      <button tabIndex={-1} className='cancel small secondary inverted' onClick={this.props.onClick}>
        <i className="ion-close"></i>
        <span>Decline</span>
      </button>
    );
  }
});

module.exports = DeclineBtn;


