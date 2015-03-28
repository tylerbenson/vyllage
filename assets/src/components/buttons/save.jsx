var React = require('react');

var SaveBtn = React.createClass({

  saveHandler: function() {
    if(this.props.saveHandler) {
      this.props.saveHandler();
    }
  },

  render: function() {
    return (
      <button className='button button-inverted' onClick={this.saveHandler}>
        <i className="icon ion-checkmark"></i>
        Save
      </button>
    );
  }
});

module.exports = SaveBtn;


