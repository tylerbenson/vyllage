var React = require('react');

var Avatar = React.createClass({
	getDefaultProps: function(){
		return {
			src: '/images/user.png',
			size: 80
		};
	},
	render: function() {
		var url = this.props.src + '?s=' + this.props.size * 2;
		var styles = {
			backgroundImage: 'url(' + url + ')',
			height: this.props.size,
			width: this.props.size
		};

		return (
			<div className="avatar" style={styles}></div>
		);
	}

});

module.exports = Avatar;