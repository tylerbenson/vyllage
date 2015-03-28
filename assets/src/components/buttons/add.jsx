var React = require('react');

var AddBtn = React.createClass({

  addSection: function() {
    if(this.props.addSection) {
      this.props.addSection();
    }
  },

  render: function() {
    return (
      <button className='inverted' onClick={this.addSection}>
        <i className='icon ion-plus'></i>
        Add
      </button>
    );
  }
});

module.exports = AddBtn;
