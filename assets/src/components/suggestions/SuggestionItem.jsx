var React = require('react');
var Reflux = require('reflux');
var SuggestionStore = require('./store');
var Actions = require('./actions');

var SuggestionItem = React.createClass({
	mixins: [Reflux.connect(SuggestionStore)],
	render: function() {
		var badge = <i className="sponsored badge ion-arrow-graph-up-right"></i>;
		return (
			<div className="suggestion">
				<div className="avatar" style={{"backgroundImage" : "url(" + this.props.user.avatar + ")"}}></div>
				<div className="info">
					<div className="name">
						{this.props.user.name}
						{this.props.user.is_sponsored ? badge : null}
					</div>
					<div className="tagline">{this.props.user.tagline}</div>
				</div>
				<div className="actions">
					<button className="normal-caps small inverted get-feedback">
						<i className="ion-person-add"></i>
						<span>Get Feedback</span>
					</button>
				</div>
			</div>
		);
	}

});

module.exports = SuggestionItem;