var React = require('react');
var Reflux = require('reflux');
var GetFeedbackStore = require('./store');
var FeatureToggle = require('../util/FeatureToggle');
var FacebookInvite = require('./FacebookInvite');
var Actions = require('./actions');

var InviteOptions = React.createClass({
	mixins: [Reflux.connect(GetFeedbackStore)],
	select: function(type, e) {
		Actions.setInviteType(type);
	},
	render: function() {
		return (
			<FeatureToggle name="SHARE_RESUME">
			<div className="invite-options">
				<div className="content">
		      <ul className="channels">
		        <li>
		          <button onClick={this.select.bind(this, 'link')}
		          className={(this.state.inviteType === 'link' ? 'primary active' : 'secondary') + ' flat'}
		          ><i className="ion-android-share-alt"></i> URL</button>
		        </li>

		        <li>
		          <button onClick={this.select.bind(this, 'form')}
		          className={(this.state.inviteType === 'form' ? 'primary active' : 'secondary') + ' flat'}
		          ><i className="ion-email"></i> E-mail</button>
		        </li>

						<FeatureToggle name="SUGGESTIONS">
			        <li>
			          <button onClick={this.select.bind(this, 'suggestions')}
			          className={(this.state.inviteType === 'suggestions' ? 'primary active' : 'secondary') + ' flat'}
			          ><i className="ion-ios-people"></i> Suggestions</button>
			        </li>
			      </FeatureToggle>
		      </ul>
		    </div>
	    </div>
	    </FeatureToggle>
		);
	}
});

module.exports = InviteOptions;