var React = require('react');
var Modal = require('../modal');
var actions = require('./actions');

var DeleteAccount = React.createClass({
  getInitialState: function () {
    return {
      isOpen: false,
      isDeleted: false
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
  deleteAccount: function (e) {
    var self = this;
    actions.deleteAccount(function(){
      self.setState({
        isOpen: false,
        isDeleted: true
      });
    });
  },
  redirectToLogin: function(){
    this.setState({
      isDeleted: false
    });
    window.location = "/login";
  },
  render: function () {
    return (
      <div style={{display: 'inline-block'}}>
        <button
          className='flat small secondary'
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
            <button className="small inverted" onClick={this.deleteAccount}>
              <i className="ion-trash-a"></i>
              Delete
            </button>
            <button className="small inverted secondary" onClick={this.closeModal}>
              Cancel
            </button>
          </div>
        </Modal>
        <Modal isOpen={this.state.isDeleted} close={this.redirectToLogin}>
          <div className="header">
            <div className="title">
              <h1>Account Deleted</h1>
            </div>
            <div className="actions">
              <button className="secondary flat icon" onClick={this.redirectToLogin}>
                <i className="ion-close"></i>
              </button>
            </div>
          </div>
          <div className="content">
            <p>Your account was successfully deleted, closing this modal will redirect you to the login page.</p>
          </div>
          <div className="footer">
            <button className="small inverted" onClick={this.redirectToLogin}>
              <i className="ion-checkmark"></i>
              Okay
            </button>
          </div>
        </Modal>
      </div>
    );
  }
});

module.exports = DeleteAccount;