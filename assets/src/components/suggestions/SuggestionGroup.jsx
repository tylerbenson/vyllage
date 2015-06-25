var React = require('react');
var SuggestionItem = require('./SuggestionItem');

var SuggestionGroup = React.createClass({

	render: function() {
		return (
			<div className="suggestion-group">
				<div className="title">
					Users with matching <strong>Career Interests</strong>
				</div>
				<SuggestionItem />
				<SuggestionItem />
				<SuggestionItem />
				<SuggestionItem />
				<button className="view-more">View More</button>
			</div>
		);
	}

});

module.exports = SuggestionGroup;