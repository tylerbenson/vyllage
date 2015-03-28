var React = require('react');

var SaveBtn = React.createClass({
  render: function() {
    return (
      <button className='small' onClick={this.props.onClick}>
        <i className="icon ion-checkmark"></i>
        Save
      </button>
    );
  }
});

module.exports = SaveBtn;


