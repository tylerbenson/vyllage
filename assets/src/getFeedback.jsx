var React = require('react');
var Reflux = require('reflux');
var Header = require('./components/header');
var Footer = require('./components/footer');
var InviteOptions = require('./components/getFeedback/InviteOptions');
var ShareableLink = require('./components/getFeedback/ShareableLink');
var Suggestions = require('./components/getFeedback/suggestions/Suggestions');
var InviteForm = require('./components/getFeedback');
var GetFeedbackStore = require('./components/getFeedback/store');
var actions = require('./components/getFeedback/actions');
require('./components/intercom');

React.initializeTouchEvents(true);
var GetFeedback = React.createClass({
	mixins: [Reflux.connect(GetFeedbackStore)],
	render: function(){
		var InviteType = null;

		switch(this.state.inviteType) {
			case 'link':
				InviteType = <ShareableLink url={this.state.shareableLink} />;
				break;
			case 'form':
				InviteType = <InviteForm />;
				break;
			case 'suggestions':
				InviteType = <Suggestions />;
				break;
			default:
				InviteType = <InviteForm />;
		}

		return (
			<div>
			<div className="banner">
				<div className="content">
					<i className="header-icon ion-person-stalker"></i>
					<h1>Get Feedback</h1>
					<p>Have a better resumé by inviting your friends for feedback.</p>
				</div>
			</div>
			<InviteOptions />
				{InviteType}
			</div>
		);
	}
});

React.render(<GetFeedback />, document.getElementById('get-feedback'));
