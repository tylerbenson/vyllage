var React = require('react');

var EditBtn = React.createClass({

  editHandler: function() {
    if(this.props.editHandler) {
      this.props.editHandler();
    }
  },

  render: function() {
    return (
      <button className='button inverted' onClick={this.editHandler}>
        <i className="icon ion-edit"></i>
        Edit
      </button>
    );
  }
});

module.exports = EditBtn;
