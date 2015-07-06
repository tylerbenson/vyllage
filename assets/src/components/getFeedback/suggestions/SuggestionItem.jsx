var React = require('react');
var Reflux = require('reflux');
var GetFeedbackStore = require('../store');
var Actions = require('../actions');

var SuggestionItem = React.createClass({
	mixins: [Reflux.connect(GetFeedbackStore)],
	requestForFeedback: function(index, e) {
		Actions.requestForFeedback(index);
	},
	render: function() {
		var badge = <i className="sponsored badge ion-arrow-graph-up-right"></i>;
		return (
			<div className={(this.props.user.is_sponsored ? 'sponsored ' : '') + 'suggestion'}>
				<div className="avatar" style={{"backgroundImage" : "url(" + this.props.user.avatar + ")"}}></div>
				<div className="info">
					<div className="name">
						{this.props.user.name}
						{this.props.user.is_sponsored ? badge : null}
					</div>
					<div className="tagline">{this.props.user.tagline}</div>
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