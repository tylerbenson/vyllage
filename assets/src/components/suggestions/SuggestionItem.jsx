var React = require('react');

var SuggestionItem = React.createClass({
	render: function() {
		return (
			<div className="suggestion">
				<div className="avatar"></div>
				<div className="name">Darth Vader</div>
				<div className="tagline">I am your father</div>
				<button className="normal-caps small inverted get-feedback">
					<i className="ion-person-stalker"></i>
					<span>Get Feedback</span>
				</button>
			</div>
		);
	}

});

module.exports = SuggestionItem;