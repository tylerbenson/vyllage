var React = require('react');

var TwitterInvite = React.createClass({
	shareOnTwitter: function(){
		window.open(
		"https://twitter.com/intent/tweet?text=See%20my%20resume.&url=" + this.props.url + "&via=vyllagehq&original_referer=" +this.props.url,
		'Share on Twitter',
		'height=260,width=630' 
		);
	},
	render: function() {
		return (
			<span onClick={this.shareOnTwitter}>
				{this.props.children}
			</span>
		);
	}

});

module.exports = TwitterInvite;