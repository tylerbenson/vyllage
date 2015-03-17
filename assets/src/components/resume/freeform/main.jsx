var React = require('react');

var FreeformMain = React.createClass({

	goToEditMode: function() {
		if (this.props.goToEditMode) {
			this.props.goToEditMode();
		}
	},

	render: function() {
		return (
			<div className="nonEditable"  onClick={this.goToEditMode}>

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