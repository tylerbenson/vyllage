var React = require('react');
var Modal = require('../modal');

var RegisterButton = React.createClass({
  getInitialState: function () {
    return {
      isOpen: false,
      isFormVisible: false
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
  toggleFormVisibility: function(e){
    e.preventDefault();
    this.setState({
      isFormVisible: !this.state.isFormVisible
    });
  },
  render: function () {
    var token = document.getElementById("meta_token").content || "";

    return (
      <div style={{display: 'inline-block'}}>
        <button className="landing" onClick={this.openModal}>Join Us Now</button>
        <Modal isOpen={this.state.isOpen} close={this.closeModal}>
            <div className="content">
              <h1 className="centered">Register</h1>
              <p className="centered">You can use your social media account or you can setup your own profile.</p>

              <div className="social-login">
                <form name="fb_signin" id="fb_signin" action="/signin/facebook" method="POST" className="centered">
                  <input type="hidden" name="_csrf" value={token} />
                  <input type="hidden" name="scope" value="email" />
                  <button type="submit" className="facebook">
                    <i className="ion-social-facebook"></i>
                    <span>Facebook</span>
                  </button>
                  <button onClick={this.toggleFormVisibility} className="normal-caps secondary">
                    <i className="ion-person"></i>
                    <span>Setup Profile</span>
                  </button>
                </form>
              </div>

            <form action="/register" id="register-form" method="post" className={this.state.isFormVisible ? '' : 'hidden'}>
              <input type="hidden" name="_csrf" value={token} />
              <label>First Name</label>
              <input type="text" name="firstName" className="padded" />

              <label>Last Name</label>
              <input type="text" name="lastName" className="padded" />

              <label>E-mail</label>
              <input type="text" name="email" className="padded" />

              <label>Password</label>
              <input type="password" name="password" className="padded" />

              <div className="checkbox">
                <input type="checkbox" name="receiveAdvice" defaultChecked="checked" />
                <p>Receive free professional career advice from Vyllage's partner schools.</p>
              </div>

              <div className="actions">
                <button type="submit" className="padded">Register</button>
                <button onClick={this.closeModal} className="padded flat secondary">Cancel</button>
              </div>
            </form>
          </div>
        </Modal>
      </div>
    );
	}
});

module.exports = RegisterButton;