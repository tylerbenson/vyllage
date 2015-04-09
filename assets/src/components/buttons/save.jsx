var React = require('react');

var SaveBtn = React.createClass({
  render: function() {
    return (
      <button tabIndex={-1} className='small' onClick={this.props.onClick}>
        <i className="icon ion-checkmark"></i>
        Save
      </button>
    );
  }
});

module.exports = SaveBtn;


