var React = require('react');

var FreeformMain = React.createClass({  

	render: function() {
		return (
			<div className="nonEditable">

				<div className="main">
					<div className="paragraph">
						<p className="freeform-description">
						   {this.props.description}
						</p>
					</div>
				</div>
			</div>
		);
	}
});

module.exports = FreeformMain;