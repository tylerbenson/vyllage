var React = require('react');

var AcceptBtn = React.createClass({
  render: function() {
    return (
      <button tabIndex={-1} className='small' onClick={this.props.onClick}>
        <i className="icon ion-checkmark"></i>
        Accept
      </button>
    );
  }
});

module.exports = AcceptBtn;


