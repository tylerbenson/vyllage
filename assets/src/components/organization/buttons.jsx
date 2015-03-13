var React = require('react');

var Buttons = React.createClass({   

	saveHandler: function(event) {
		event.preventDefault();
		event.stopPropagation();

		if (this.props.save) {
			this.props.save();
		}
	},    

	cancelHandler: function(event) {
		event.preventDefault();
		event.stopPropagation();

		if (this.props.cancel) {
			this.props.cancel();
		}
	},

	render: function() {
		return (
			<div>
				<button className="save-btn"  disabled={this.props.valid ? '' : 'disabled'}  onClick={this.saveHandler}>save</button>
				<button className="cancel-btn" onClick={this.cancelHandler}>cancel</button>
			</div>
		);
	}
});

module.exports = Buttons;