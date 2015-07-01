var React = require('react');
var Reflux = require('reflux');
var Header = require('./components/header');
var Footer = require('./components/footer');
var InviteOptions = require('./components/getFeedback/InviteOptions');
var ShareableLink = require('./components/getFeedback/ShareableLink');
var Suggestions = require('./components/suggestions/Suggestions');
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
			<InviteOptions />
				{InviteType}
			</div>
		);
	}
});

React.render(<GetFeedback />, document.getElementById('get-feedback'));
