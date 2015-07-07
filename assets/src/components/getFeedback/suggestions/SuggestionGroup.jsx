var React = require('react/addons');
var Reflux = require('reflux');
var GetFeedbackStore = require('../store');
var Actions = require('../actions');
var SuggestionItem = require('./SuggestionItem');
var ReactCSSTransitionGroup = React.addons.CSSTransitionGroup;

var SuggestionGroup = React.createClass({
	mixins: [Reflux.connect(GetFeedbackStore)],
	componentWillMount: function(){
		if(this.state.recommendations.length<1){
			Actions.getRecommendations();
		}
	},
	render: function() {
		var index = 0;
		var suggestionItems = this.state.recommendations.map(function(user){
			return (
				<SuggestionItem key={user.userId} index={index++} user={user} />
			);
		});

		var empty = (
			<div className="empty">We cannot find any more users matching your profile. Update your information and invite your friends to get more suggestions.</div>
		);

		return (
			<div className="suggestion-group">
				{(this.state.recommendations.length > 0 ?
					<div>
						<ReactCSSTransitionGroup transitionName="suggestion" transitionAppear={false}>
							{suggestionItems}
						</ReactCSSTransitionGroup>
						<button className="view-more">
							<i className="ion-chevron-down"></i>
							<span>Load More Suggestions</span>
						</button>
					</div>
				: empty)}
			</div>
		);
	}

});

module.exports = SuggestionGroup;