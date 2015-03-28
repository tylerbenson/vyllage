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
      <div style={{display: 'inline-block'}}>
        <button 
          className='small icon'
          onClick={this.onOpen}>
          <i className='ion-trash-a'></i>
          
        </button>
        <Modal isOpen={this.state.isOpen}> 
          <div>
            Do you really want to delete this section ?
          </div>
          <div className='pull right actions'>
            <button className='small inverted' onClick={this.deleteSection}>Delete</button>
            <button className='small secondary inverted' onClick={this.onClose}>Cancel</button>
          </div>
        </Modal>
      </div>

    );
  }
});

module.exports = DeleteSection;