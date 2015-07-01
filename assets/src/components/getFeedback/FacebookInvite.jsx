var React = require('react');

var FacebookInvite = React.createClass({
	shareOnFacebook: function(){
		FB.ui({
		  method: 'share',
		  href: this.props.url,
		}, function(response){});
	},
	render: function() {
		return (
			<button onClick={this.shareOnFacebook} className="facebook"><i className="ion-social-facebook"></i> Facebook</button>
		);
	}

});

module.exports = FacebookInvite;