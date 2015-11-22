var React = require('react');
var Modal = require('../modal');

var LoginButton = React.createClass({
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
  render: function () {

    return (
      <div style={{display: 'inline-block'}}>
			<button onClick={this.openModal} className="landing alternate">Sign In</button>
        <Modal isOpen={this.state.isOpen} close={this.closeModal}>
          <div className="content">
          <form action="/login" method="post">
            <h1 className="centered">Sign In</h1>
            <p className="centered">Welcome back to Vyllage!</p>
            <label>E-mail</label>
            <input type="text" className="padded" name="email" />
            <label>Password</label>
            <input type="password" className="padded" name="password" />
            <a href="/account/reset-password" className="forgot">Forgot Password?</a>
            <div className="actions">
              <button className="padded">Submit</button>
              <button onClick={this.closeModal} className="padded flat secondary">Cancel</button>
            </div>
          </form>
          </div>
        </Modal>
      </div>
    );
  }
});

module.exports = LoginButton;