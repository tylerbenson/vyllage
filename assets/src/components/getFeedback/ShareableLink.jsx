var React = require('react');
var Reflux = require('reflux');
var GetFeedbackStore = require('./store');
var Clipboard = require('react-zeroclipboard');
var SuggestionSidebar = require('./suggestions/SuggestionSidebar');
var FeatureToggle = require('../util/FeatureToggle');
var FacebookInvite = require('./FacebookInvite');
var actions = require('./actions');

var ShareableLink = React.createClass({
	mixins: [Reflux.connect(GetFeedbackStore)],
	getInitialState: function(){
		return {
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
	render: function(){
		var isCopied = this.state.isCopied;
		var isReady = this.state.isReady;
		var classes = 'padded copy button';
		var message = 'Copy';
		var icon = 'ion-android-clipboard';

		if(isCopied) {
			icon = 'ion-checkmark';
			message = 'Copied';
			classes += ' copied';
		}
		if(!isReady) {
			icon = 'ion-load-d';
			message = 'Wait..';
			classes += ' disabled';
		}

		return (
			<div className="sections">
				<div className="shareable section">
					<div className="container">
						<div className="content">
							<p className="tip">Paste this link anywhere on the web to share.</p>
							<input id="shareable-link" className="padded" type="text" value={this.props.url} readOnly />
							<div className={classes}>
								<Clipboard text={this.state.shareableLink} onAfterCopy={this.copyHandler} onReady={this.readyHandler}>
									<button className="invisible"></button>
		            </Clipboard>
		            <i className={icon}></i>
								{message}
							</div>
							<FeatureToggle name="FACEBOOK_SDK">
								<div className="or">
									<span>OR</span>
								</div>
								<p className="tip">Click the following buttons to share:</p>
				        <FacebookInvite url={this.state.shareableLink}>
				        	<button className="facebook"><i className="ion-social-facebook"></i> Facebook</button>
				        </FacebookInvite>
			        </FeatureToggle>
						</div>
					</div>
				</div>
				<FeatureToggle name="SUGGESTIONS">
					<SuggestionSidebar />
				</ FeatureToggle>
			</div>
		);
	}
});

module.exports = ShareableLink;