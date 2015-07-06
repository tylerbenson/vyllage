var React = require('react');
var Reflux = require('reflux');
var GetFeedbackStore = require('../store');
var Actions = require('../actions');
var SuggestionItem = require('./SuggestionItem');

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
				<SuggestionItem key={index++} user={user} />
			);
		});

		return (
			<div className="suggestion-sidebar section">
				<div className="container">
					<div className="content">
						<div className="title">Suggestions for You</div>
						{suggestionItems}
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