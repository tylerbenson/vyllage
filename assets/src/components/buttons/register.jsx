var React = require('react');
var Modal = require('../modal');

var RegisterButton = React.createClass({
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
    var token = document.getElementById("meta_token").content || "";

    return (
      <div style={{display: 'inline-block'}}>
        <button className="landing" onClick={this.openModal}>Join Us Now</button>
        <Modal isOpen={this.state.isOpen} close={this.closeModal}>
          <div className="content">
          <form action="/register" method="post">
            <input type="hidden" name="_csrf" value={token} />
            <div className="content">
              <h1 className="centered">Register</h1>
              <p className="centered">Fill out the fields below to proceed. </p>

              <label>First Name</label>
              <input type="text" name="firstName" className="padded" />

              <label>Last Name</label>
              <input type="text" name="lastName" className="padded" />

              <label>E-mail</label>
              <input type="text" name="email" className="padded" />

              <label>Password</label>
              <input type="password" name="password" className="padded" />

              <div className="checkbox">
                <input type="checkbox" name="receiveAdvice" checked="checked" />
                <p>Receive free professional career advice from Vyllage's partner schools.</p>
              </div>

              <div className="actions">
                <button type="submit" className="padded">Register</button>
                <button onClick={this.closeModal} className="padded flat secondary">Cancel</button>
              </div>
            </div>
          </form>
          </div>
        </Modal>
      </div>
    );
	}
});

module.exports = RegisterButton;