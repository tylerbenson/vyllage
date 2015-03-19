var React = require('react');
var actions = require('../actions');

var  FreeformEdit = React.createClass({ 
	getInitialState: function() {
		return {description:this.props.description}; 
	},
	getDefaultProps: function () {
		return {
			placeholder: 'tell us ....'
		}
	},
	handleChange: function(e) {
		e.preventDefault();
		this.setState({description: e.target.value});
	},
	saveHandler: function(e) {
		var section = this.props.section;
		section.description = this.state.description;
		actions.putSection(section);
	},
	cancelHandler: function(e) {
		e.preventDefault();
		this.setState({description:this.props.description});
		actions.disableEditMode(this.props.section.sectionId);
	},
	render: function() {
		var className = this.props.className + '-edit';
		return (
			<div>
				<a className='button' onClick={this.saveHandler}>Save</a>
				<a className='button' onClick={this.cancelHandler}>Cancel</a>
				<textarea className={className} 
					placeholder={this.props.placeholder}
					onChange={this.handleChange} value ={this.state.description} >
				</textarea>
			</div>
		);
	}
});

module.exports = FreeformEdit;