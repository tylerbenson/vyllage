var React = require('react');

var CancelBtn = React.createClass({
  render: function() {
    return (
      <button tabIndex={-1} className='small secondary inverted' onClick={this.props.onClick}>
        Cancel
      </button>
    );
  }
});

module.exports = CancelBtn;


