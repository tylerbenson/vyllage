var React = require('react');
var Actions = require('../organization/actions');


var ButtonsContainer = React.createClass({  

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

	hide: function(event) {
		event.preventDefault();
		event.stopPropagation();

	},   

	remove: function(event) {
		event.preventDefault();
		event.stopPropagation();
		Actions.deleteSection(this.props.sectionId);
	},   

	render: function() {
		return (
			<div className="buttons-container">
				<button className='save-btn' disabled={this.props.valid ? '' : 'disabled'} onClick={this.saveHandler} >save</button>
				<button className="cancel-btn" onClick={this.cancelHandler}>cancel</button>
				<a href="" className="delete-btn"  onClick={this.hide}>hide </a> | 
				<a href=""className="delete-btn"  onClick={this.remove}>remove </a>
			</div>
		); 
	}
});

module.exports = ButtonsContainer;