var React = require('react');

var EditBtn = React.createClass({
  render: function() {
    return (
      <button className='small inverted edit' onClick={this.props.onClick}>
        <i className="ion-edit"></i>
        Edit
      </button>
    );
  }
});

module.exports = EditBtn;
