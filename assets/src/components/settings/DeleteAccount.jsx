var React = require('react');
var Modal = require('../modal');
var actions = require('./actions');

var DeleteAccount = React.createClass({
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
  handleSubmit: function(e){
    document.getElementById('deleteForm').submit();
  },
  render: function () {
    return (
      <div style={{display: 'inline-block'}}>
        <button
          className='flat small destructive normal-caps'
          onClick={this.openModal}>
          <i className='ion-trash-a'></i>
          Delete Account
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
            <p>Are you sure you want to delete your account?</p>
          </div>
          <div className="footer">
            <button onClick={this.handleSubmit} className="small inverted">
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

module.exports = DeleteAccount;