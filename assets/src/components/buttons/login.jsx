var React = require('react');
var Modal = require('../modal');

var LoginButton = React.createClass({
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
			<button onClick={this.openModal} className="landing alternate">Sign In</button>
        <Modal isOpen={this.state.isOpen} close={this.closeModal}>
          <div className="content">
          <h1 className="centered">Sign In</h1>
          <p className="centered">Welcome back! You can use your social media account or your registered e-mail to sign in.</p>

          <div className="social-login">
            <form name="fb_signin" id="fb_signin" action="/signin/facebook" method="POST" className="centered">
              <input type="hidden" name="_csrf" value={token} />
              <input type="hidden" name="scope" value="email" />
              <button type="submit" className="facebook">
                <i className="ion-social-facebook"></i>
                <span>Facebook</span>
              </button>
              <button onClick={this.toggleFormVisibility} className="normal-caps secondary">
                <i className="ion-email"></i>
                <span>E-mail</span>
              </button>
            </form>
          </div>

          <form action="/login" id="login-form" method="post" className={this.state.isFormVisible ? '' : 'hidden'}>
            <input type="hidden" name="_csrf" value={token} />
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