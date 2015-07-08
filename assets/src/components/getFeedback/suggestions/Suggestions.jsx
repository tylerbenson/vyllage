var React = require('react');
var SuggestionGroup = require('./SuggestionGroup');

var Suggestions = React.createClass({
	render: function(){
		return (
			<div className="sections">
				<div className="section suggestions">
					<div className="container">
						<div className="content">
							<SuggestionGroup />
						</div>
					</div>
				</div>
			</div>
		);
	}
});

module.exports = Suggestions;