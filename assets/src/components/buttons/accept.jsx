var React = require('react');
var Modal = require('../modal');

var AcceptBtn = React.createClass({
  render: function() {
    return (
      <button tabIndex={-1} className='small' onClick={this.props.onClick}>
        <i className="icon ion-checkmark"></i>
        <span>Accept</span>
      </button>
    );
  }
});

module.exports = AcceptBtn;


