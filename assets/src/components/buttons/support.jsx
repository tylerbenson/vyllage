var React = require('react');

var Support = React.createClass({
	render: function() {
		return (
			<a href="javascript:$zopim.livechat.window.show();" className="flat support button">
				<i className="ion-help-buoy"></i>
        <span>Support</span>
			</a>
		);
	}

});

module.exports = Support;