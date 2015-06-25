var React = require('react');
var Reflux = require('reflux');
var SuggestionStore = require('./store');
var Actions = require('./actions');
var SuggestionGroup = require('./SuggestionGroup');

var Suggestions = React.createClass({
	mixins: [Reflux.connect(SuggestionStore)],
	componentDidMount: function(){
		Actions.getSuggestions();
	},
	render: function(){
		var suggestionGroups = this.state.suggestions.map(function (group) {
      return (
        <SuggestionGroup title={group.title} users={group.users} />
      );
    });

		return (
			<div className="sections">
				<div className="section suggestions">
					<div className="container">
						<div className="content">
							{suggestionGroups}
						</div>
					</div>
				</div>
			</div>
		);
	}
});

module.exports = Suggestions;