var React = require('react');
var SuggestionItem = require('./SuggestionItem');

var SuggestionGroup = React.createClass({

	render: function() {
		var suggestionItems = this.props.users.map(function(user){
			return (
				<SuggestionItem name={user.name} tagline={user.tagline} />
			);
		});

		return (
			<div className="suggestion-group">
				<div className="title">
					{this.props.title}
				</div>
				{suggestionItems}
				<button className="view-more">View More</button>
			</div>
		);
	}

});

module.exports = SuggestionGroup;