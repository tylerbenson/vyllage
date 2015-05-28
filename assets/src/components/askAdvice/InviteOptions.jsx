var React = require('react');
var Reflux = require('reflux');
var AskAdviceStore = require('./store');
var actions = require('./actions');

var InviteOptions = React.createClass({
	mixins: [Reflux.connect(AskAdviceStore)],
	select: function(type, e) {
		actions.setInviteType(type);
	},
	shareOnFacebook: function(){
		FB.ui({
		  method: 'share',
		  href: this.props.url,
		}, function(response){});
	},
	render: function() {
		return (
			<div className="banner content">
	      <ul className="channels">
	        <li>
	          <button onClick={this.select.bind(this, 'link')}
	          className={(this.state.inviteType === 'link' ? 'primary active' : 'secondary') + ' flat'}
	          ><i className="ion-android-share-alt"></i> Copy Shareable Link</button>
	        </li>
	        <li>
	          <button onClick={this.select.bind(this, 'form')}
	          className={(this.state.inviteType === 'form' ? 'primary active' : 'secondary') + ' flat'}
	          ><i className="ion-email"></i> Invite Through E-mail</button>
	        </li>
	        <li>
	          <button onClick={this.shareOnFacebook} className="secondary flat facebook"><i className="ion-social-facebook"></i> Share on Facebook</button>
	        </li>
	      </ul>
	    </div>
		);
	}
});

module.exports = InviteOptions;