var React = require('react');
var SuggestionItem = require('./SuggestionItem');

var SuggestionGroup = React.createClass({
	componentWillMount: function() {

	},
	render: function() {
		var suggestionItems = this.props.users.map(function(user){
			return (
				<SuggestionItem avatar={user.avatar} key={user.name} name={user.name} tagline={user.tagline} />
			);
		});

		return (
			<div className="suggestion-group">
				{suggestionItems}
				<button className="view-more">View More</button>
			</div>
		);
	}

});

module.exports = SuggestionGroup;