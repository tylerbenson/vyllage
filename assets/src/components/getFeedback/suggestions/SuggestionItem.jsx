var React = require('react');
var Reflux = require('reflux');
var GetFeedbackStore = require('../store');
var Actions = require('../actions');
var Avatar = require('../../avatar');

var SuggestionItem = React.createClass({
	mixins: [Reflux.connect(GetFeedbackStore)],
	requestForFeedback: function(index, e) {
		Actions.requestForFeedback(index);
	},
	render: function() {
		var badge = <i className="sponsored badge ion-arrow-graph-up-right"></i>;
		var user = this.props.user;
		return (
			<div className={(user.advisor ? 'sponsored ' : '') + 'suggestion'}>
				<Avatar src={user.avatarUrl} size={this.props.avatarSize} />
				<div className="requested avatar" style={{height: this.props.avatarSize, width: this.props.avatarSize}}></div>
				<div className="info">
					<div className="name">
						{user.firstName + ' ' + user.lastName}
						{user.advisor ? badge : null}
					</div>
					<div className="tagline">{user.tagline ? user.tagline : 'Vyllage User'}</div>
					<div className="requested">
						Request for feedback sent to {user.firstName + ' ' + user.lastName}!
					</div>
				</div>
				<div className="actions">
					<button onClick={this.requestForFeedback.bind(this, this.props.index)} className="normal-caps small inverted get-feedback">
						<i className="ion-person-add"></i>
						<span>Get Feedback</span>
					</button>
				</div>
			</div>
		);
	}

});

module.exports = SuggestionItem;