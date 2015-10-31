var React = require('react');

var Support = React.createClass({
	openSupport: function() {
		if($zopim.livechat instanceof Object){
			$zopim.livechat.window.show();
		}
		//Fallback to e-mail if Zopim is not available
		else {
			window.location = "mailto:support@vyllage.com";
		}
	},
	render: function() {
		return (
			<button onClick={this.openSupport} className="flat support">
				<i className="ion-help-buoy"></i>
        <span>Support</span>
			</button>
		);
	}

});

module.exports = Support;