var React = require('react');

var EditBtn = React.createClass({

  editHandler: function() {
    if(this.props.editHandler) {
      this.props.editHandler();
    }
  },

  render: function() {
    return (
      <button className='small inverted' onClick={this.editHandler}>
        <i className="ion-edit"></i>
        {"Edit"}
      </button>
    );
  }
});

module.exports = EditBtn;
