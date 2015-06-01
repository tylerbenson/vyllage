var React = require('react');
var request = require('superagent');
var urlTemplate = require('url-template');
var endpoints = require('../endpoints');

var FeatureToggle = React.createClass({
	getInitialState: function(){
		return {
			isActive: false
		}
	},
	componentWillMount: function() {
		var url = urlTemplate
			.parse(endpoints.togglz)
			.expand({feature: this.props.name});

		request
			.get(url)
			.end(function(err, res) {
				if(res.ok) {
					this.setState({
						isActive: res.text === 'true'
					});
				}
				else {
					this.setState({
						isActive: false
					});
				}
			}.bind(this));
	},
	render: function() {
		return (
			<span>
			{this.state.isActive? this.props.children : null}
			</span>
			);
	}
});

module.exports = FeatureToggle;