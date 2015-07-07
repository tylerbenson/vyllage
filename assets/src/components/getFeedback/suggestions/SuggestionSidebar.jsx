var React = require('react/addons');
var Reflux = require('reflux');
var GetFeedbackStore = require('../store');
var Actions = require('../actions');
var SuggestionItem = require('./SuggestionItem');
var ReactCSSTransitionGroup = React.addons.CSSTransitionGroup;

var SuggestionSidebar = React.createClass({
	mixins: [Reflux.connect(GetFeedbackStore)],
	componentWillMount: function(){
		Actions.getRecommendations();
	},
	viewMore: function(){
		Actions.setInviteType('suggestions');
	},
	render: function() {
		var index = 0;
		var suggestionItems = this.state.recommendations.map(function(user){
			return (
				<SuggestionItem key={user.id} index={index++} user={user} />
			);
		});

		return (
			<div className="suggestion-sidebar section">
				<div className="container">
					<div className="content">
						<div className="title">Suggestions for You</div>
						{(this.state.recommendations.length > 0 ?
						<ReactCSSTransitionGroup transitionName="suggestion" transitionAppear={false}>
							{suggestionItems}
						</ReactCSSTransitionGroup>
						: null)}
						<button onClick={this.viewMore} className="view-more">
							<span>View More</span>
						</button>
					</div>
				</div>
			</div>
		);
	}

});

module.exports = SuggestionSidebar;