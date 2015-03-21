var React = require('react');
var Modal = require('../modal');
var actions = require('./actions');

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
  deleteSection: function (e) {
    e.preventDefault();
    this.setState({isOpen: false});
    actions.deleteSection(this.props.sectionId);
  },
  render: function () {
    return (
      <div className={this.props.className}>
        <i className='icon ion-android-delete' onClick={this.onOpen}></i>
        <Modal isOpen={this.state.isOpen}> 
          <a className='close-button' onClick={this.onClose}>&times;</a>
          Do you really want to delete this section ?
          <div className='row'>
            <a className='button' onClick={this.deleteSection}>Delete</a>
            <a className='button' onClick={this.onClose}>Cancel</a>
          </div>
        </Modal>
      </div>
    );
  }
});

module.exports = DeleteSection;