var React = require('react');
var Modal = require('../modal');

var DeleteSection = React.createClass({
  getInitialState: function () {
    return {
      isOpen: false
    }
  },
  onClose: function (e) {
    e.preventDefault();
    this.setState({isOpen: false});
  },
  onOpen: function (e) {
    e.preventDefault();
    this.setState({isOpen: true});
  },
  render: function () {
    return (
      <div className={this.props.className}>
        <i className='icon ion-android-delete' onClick={this.onOpen}></i>
        <Modal isOpen={this.state.isOpen}> 
          <a className='close-button' onClick={this.onClose}>&times;</a>
          some text  
        </Modal>
      </div>
    );
  }
});

module.exports = DeleteSection;