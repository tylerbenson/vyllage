var React = require('react');
var Reflux = require('reflux');
var GetFeedbackStore = require('./store');
var Clipboard = require('react-zeroclipboard');
var Modal = require('../modal');
var FeatureToggle = require('../util/FeatureToggle');
var FacebookInvite = require('./FacebookInvite');
var actions = require('./actions');

var ShareButton = React.createClass({
	mixins: [Reflux.connect(GetFeedbackStore)],
	getInitialState: function(){
		return {
			isShareModalOpen: false,
			isReady: false,
			isCopied: false
		}
	},
	componentWillMount: function(){
		actions.getShareableLink();
	},
	copyHandler: function(){
		this.setState({
			isCopied: true
		});
		setTimeout(function(){
			this.setState({
				isCopied: false
			});
		}.bind(this), 2500);
	},
	readyHandler: function(){
		this.setState({
			isReady: true
		});
	},
  openModal: function() {
    this.toggleShareModal(true);
  },
  closeModal: function() {
    this.toggleShareModal(false);
  },
  toggleShareModal: function(flag){
    this.setState({
      isShareModalOpen: (flag !== undefined ? flag : !this.state.isShareModalOpen)
    });
  },
	render: function(){
		return (
			<span className="wrapper">
			<a onClick={this.openModal} className="flat print button">
          <i className="ion-android-share-alt"></i>
          <span>Share</span>
        </a>
        <Modal isOpen={this.state.isShareModalOpen} close={this.closeModal} className="medium">
          <div className="header">
            <div className="title">
              <h1>Share your Resumé!</h1>
            </div>
            <div className="actions">
              <button className="secondary flat icon" onClick={this.closeModal}>
                <i className="ion-close"></i>
              </button>
            </div>
          </div>
          <div className="content">
            <div className="subheading">Choose from the following sharing options.</div>
            <Clipboard text={this.state.shareableLink} onCopyError={function(e){console.log(e);}} onAfterCopy={this.copyHandler} onReady={this.readyHandler}>
	            <div className="option">
	            	<i className="ion-android-clipboard"></i>
	            	<h2 className="title">
	            		{!this.state.isReady ? "Generating Link.." :
	            			this.state.isCopied ? "Link Copied!" :
	            			"Copy to Clipboard"}
	            	</h2>
	            	<p className="description">Paste the link anywhere in the web.</p>
	          	</div>
            </Clipboard>
            <FeatureToggle name="FACEBOOK_SDK">
	            <FacebookInvite url={this.state.shareableLink}>
	            	<div className="option">
		            	<i className="ion-social-facebook"></i>
		            	<h2 className="title">Share on Facebook</h2>
		            	<p className="description">Post on your wall and share with your friends.</p>
		          	</div>
	          	</FacebookInvite>
		        </FeatureToggle>
		        <a href="resume/get-feedback" className="option">
            	<i className="ion-person-stalker"></i>
            	<h2 className="title">More Options</h2>
            	<p className="description">Invite via e-mail or check out our suggestions.</p>
          	</a>
          </div>
        </Modal>
      </span>
		);
	}
});

module.exports = ShareButton;