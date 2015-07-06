var React = require('react');
var Reflux = require('reflux');
var SuggestionStore = require('./store');
var Actions = require('./actions');
var SuggestionItem = require('./SuggestionItem');

var SuggestionGroup = React.createClass({
	mixins: [Reflux.connect(SuggestionStore)],
	componentWillMount: function(){
		Actions.getSuggestions();
	},
	render: function() {
		var index = 0;
		var suggestionItems = this.state.suggestions.map(function(user){
			return (
				<SuggestionItem key={index++} user={user} />
			);
		});

		return (
			<div className="suggestion-group">
				{suggestionItems}
				<button className="view-more">
					<i className="ion-chevron-down"></i>
					<span>Load More Suggestions</span>
				</button>
			</div>
		);
	}

});

module.exports = SuggestionGroup;