var React = require('react');
var Reflux = require('reflux');
var GetFeedbackStore = require('../store');
var Actions = require('../actions');
var SuggestionItem = require('./SuggestionItem');
var ReactCSSTransitionGroup = React.addons.CSSTransitionGroup;

var SuggestionGroup = React.createClass({
	mixins: [Reflux.connect(GetFeedbackStore)],
	componentWillMount: function(){
		Actions.getRecommendations();
	},
	render: function() {
		var index = 0;
		var suggestionItems = this.state.recommendations.map(function(user){
			return (
				<SuggestionItem key={user.id} index={index++} user={user} />
			);
		});

		return (
			<div className="suggestion-group">
				<ReactCSSTransitionGroup transitionName="suggestion" transitionAppear={false}>
					{suggestionItems}
				</ReactCSSTransitionGroup>
				<button className="view-more">
					<i className="ion-chevron-down"></i>
					<span>Load More Suggestions</span>
				</button>
			</div>
		);
	}

});

module.exports = SuggestionGroup;