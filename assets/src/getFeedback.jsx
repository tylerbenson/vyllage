var React = require('react');
var Reflux = require('reflux');
var Header = require('./components/header');
var Footer = require('./components/footer');
var InviteOptions = require('./components/getFeedback/InviteOptions');
var ShareableLink = require('./components/getFeedback/ShareableLink');
var InviteForm = require('./components/getFeedback');
var GetFeedbackStore = require('./components/getFeedback/store');
var actions = require('./components/getFeedback/actions');
require('./components/intercom');

React.initializeTouchEvents(true);
var GetFeedback = React.createClass({
	mixins: [Reflux.connect(GetFeedbackStore)],
	componentWillMount: function(){
		actions.getShareableLink();
	},
	render: function(){
		var InviteType = null;

		if (this.state.inviteType === 'link') {
      InviteType = <ShareableLink url={this.state.shareableLink} />;
    } else if (this.state.inviteType === 'form') {
      InviteType = <InviteForm />
    }

		return (
			<div>
			<InviteOptions url={this.state.shareableLink} />
			{InviteType}
			</div>
		);
	}
});

React.render(<GetFeedback />, document.getElementById('get-feedback'));
