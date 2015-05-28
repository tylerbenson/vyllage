var React = require('react');
var Reflux = require('reflux');
var Header = require('./components/header');
var Footer = require('./components/footer');
var InviteOptions = require('./components/askAdvice/InviteOptions');
var ShareableLink = require('./components/askAdvice/ShareableLink');
var InviteForm = require('./components/askAdvice');
var AskAdviceStore = require('./components/askAdvice/store');
var actions = require('./components/askAdvice/actions');
require('./components/intercom');

React.initializeTouchEvents(true);
var AskAdvice = React.createClass({
	mixins: [Reflux.connect(AskAdviceStore)],
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
			<InviteOptions />
			{InviteType}
			</div>
		);
	}
});

React.render(<AskAdvice />, document.getElementById('ask-advice'));