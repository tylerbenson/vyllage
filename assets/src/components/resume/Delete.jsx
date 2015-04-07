var React = require('react');
var Modal = require('../modal');
var actions = require('./actions');

var DeleteSection = React.createClass({
  getInitialState: function () {
    return {
      isOpen: false
    }
  },
  closeModal: function (e) {
    e.preventDefault();
    this.setState({isOpen: false});
  },
  openModal: function (e) {
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
          className='inverted small icon'
          onClick={this.openModal}>
          <i className='ion-trash-a'></i>
        </button>
        <Modal isOpen={this.state.isOpen} close={this.closeModal}>
          <div className="header">
            <div className="title">
              <h1>Confirm Delete</h1>
            </div>
            <div className="actions">
              <button className="secondary flat icon" onClick={this.closeModal}>
                <i className="ion-close"></i>
              </button>
            </div>
          </div>
          <div className="content">
            <p>Do you want to delete this section ?</p>
          </div>
          <div className="footer">
            <button className="small inverted" onClick={this.deleteSection}>
              <i className="ion-trash-a"></i>
              Delete
            </button>
            <button className="small inverted secondary" onClick={this.closeModal}>
              Cancel
            </button>
          </div>
        </Modal>
      </div>
    );
  }
});

module.exports = DeleteSection;