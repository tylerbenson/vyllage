var React = require('react');

var Avatar = React.createClass({
	getDefaultProps: function(){
		return {
			src: '/images/user.png',
			size: 80,
			borderWidth: 2
		};
	},
	getImageURL: function(){
		var url = this.props.src;

		if(typeof url !== 'string') {
			return '/images/user.png';
		}

		if(url.indexOf('gravatar') >= 0) {
			return url + '?s=' + this.props.size * 2
					+ '&d=' + encodeURIComponent('https://www.vyllage.com/images/user.png');
		}

		return url;
	},
	render: function() {
		var url = this.getImageURL();

		var styles = {
			backgroundImage: 'url(' + url + ')',
			height: this.props.size,
			width: this.props.size,
			borderWidth: this.props.borderWidth
		};

		return (
			<div className="avatar" style={styles}>
				{this.props.children}
			</div>
		);
	}

});

module.exports = Avatar;