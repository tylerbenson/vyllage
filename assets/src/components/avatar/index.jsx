var React = require('react');

var Avatar = React.createClass({
	getDefaultProps: function(){
		return {
			src: '/images/user.png',
			size: 80,
			borderWidth: 3
		};
	},
	render: function() {
		var url = this.props.src + '?s=' + this.props.size * 2;
		var styles = {
			backgroundImage: 'url(' + url + ')',
			height: this.props.size,
			width: this.props.size,
			borderWidth: this.props.borderWidth
		};

		return (
			<div className="avatar" style={styles}></div>
		);
	}

});

module.exports = Avatar;