var React = require('react');

var AddBtn = React.createClass({
  render: function() {
    return (
      <button className='small inverted' onClick={this.props.onClick}>
        <i className='ion-plus'></i>
        Add
      </button>
    );
  }
});

module.exports = AddBtn;
