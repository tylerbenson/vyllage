var React = require('react');
var Reflux = require('reflux');
var AskAdviceStore = require('./store');
var actions = require('./actions');

var InviteOptions = React.createClass({
	mixins: [Reflux.connect(AskAdviceStore)],
	select: function(type, e) {
		actions.setInviteType(type);
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
	          {/*<button className="secondary flat"><i className="ion-social-facebook"></i> Share on Facebook</button>*/}
	          <div className="fb-share-button" data-href={this.state.shareableLink} data-layout="link"></div>
	        </li>
	      </ul>
	    </div>
		);
	}
});

module.exports = InviteOptions;