var React = require('react');
var Reflux = require('reflux');
var SuggestionStore = require('./store');
var Actions = require('./actions');
var SuggestionItem = require('./SuggestionItem');

var SuggestionSidebar = React.createClass({
	mixins: [Reflux.connect(SuggestionStore)],
	componentDidMount: function(){
		Actions.getSuggestions();
	},
	render: function() {
		var suggestionItems = this.state.suggestions.map(function(user){
			return (
				<SuggestionItem key={user.name} avatar={user.avatar} name={user.name} tagline={user.tagline} />
			);
		});

		return (
			<div className="suggestion-sidebar section">
				<div className="container">
					<div className="content">
						<div className="title">Suggestions for You</div>
						{suggestionItems}
						<button className="view-more">View More</button>
					</div>
				</div>
			</div>
		);
	}

});

module.exports = SuggestionSidebar;