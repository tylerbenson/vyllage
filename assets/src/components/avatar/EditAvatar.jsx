var React = require('react');
var Modal = require('../modal');

var EditAvatar = React.createClass({
	getInitialState: function() {
		return {
			isOpen: false
		};
	},
  closeModal: function (e) {
    e.preventDefault();
    this.setState({isOpen: false});
  },
  openModal: function (e) {
    e.preventDefault();
    this.setState({isOpen: true});
  },
	render: function() {
		return (
			<div>
				<button onClick={this.openModal} className="small edit-avatar">
					<i className="ion-android-camera"></i>
					<span>Edit</span>
				</button>
				<Modal isOpen={this.state.isOpen} close={this.closeModal}>
          <div className="header">
            <div className="title">
              <h1>Update Gravatar</h1>
            </div>
            <div className="actions">
              <button className="secondary flat icon" onClick={this.closeModal}>
                <i className="ion-close"></i>
              </button>
            </div>
          </div>
          <div className="content">
            <p>
            	Setup the avatar associated with your e-mail through Gravatar. Click to open link in a new tab.
            </p>
          </div>
          <div className="footer">
            <a className="small inverted button" target="_blank" href="//en.gravatar.com">
            	<i className="ion-android-arrow-forward"></i>
              Open Gravatar Setup
            </a>
          </div>
        </Modal>
			</div>
		);
	}

});

module.exports = EditAvatar;