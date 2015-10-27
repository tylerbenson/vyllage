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
			<span onClick={this.shareOnFacebook}>
				{this.props.children}
			</span>
		);
	}

});

module.exports = FacebookInvite;