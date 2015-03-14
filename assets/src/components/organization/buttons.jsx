var React = require('react');
var Actions = require('./actions');

var Buttons = React.createClass({   

	saveHandler: function(event) {
		event.preventDefault();
		event.stopPropagation();
		Actions.saveSection(this.props.data);
	},    

	cancelHandler: function(event) {
		event.preventDefault();
		event.stopPropagation();

		if (this.props.cancel) {
			this.props.cancel();
		}
	},

	hide: function(event) {
		event.preventDefault();
		event.stopPropagation();
	},   

	remove: function(event) {
		event.preventDefault();
		event.stopPropagation();
		Actions.deleteSection(this.props.sectionId);
	},   

	changeState: function(event) {
		event.preventDefault();
		event.stopPropagation();

		var data = this.props.data;
		if(data.state == 'shown') {
			data.state = "hidden";
		}else {
			data.state = "shown";
		}
		Actions.saveSection(data);
	},   

	render: function() {
		return (
			<div>
				<button className="save-btn"  disabled={this.props.valid ? '' : 'disabled'}  onClick={this.saveHandler}>save</button>
				<button className="cancel-btn" onClick={this.cancelHandler}>cancel</button>
				<a href="" className="delete-btn" onClick={this.changeState}> {this.props.data.state == "shown"? 'hide' : 'show'} </a> | 
				<a href=""className="delete-btn"  onClick={this.remove}>remove </a>
			</div>
		);
	}
});

module.exports = Buttons;