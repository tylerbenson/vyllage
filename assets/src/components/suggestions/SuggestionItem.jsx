var React = require('react');

var SuggestionItem = React.createClass({
	render: function() {
		return (
			<div className="suggestion">
				<div className="avatar"></div>
				<div className="info">
					<div className="name">{this.props.name}</div>
					<div className="tagline">{this.props.tagline}</div>
					<button className="normal-caps small inverted get-feedback">
						<i className="ion-person-stalker"></i>
						<span>Get Feedback</span>
					</button>
				</div>
			</div>
		);
	}

});

module.exports = SuggestionItem;