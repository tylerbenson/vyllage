var React = require('react');
var classNames = require('classnames');
var actions = require('../actions');

var  FreeformEdit = React.createClass({ 
	getInitialState: function() {
		return {description:this.props.section.description}; 
	},
	getDefaultProps: function () {
		return {
			placeholder: 'tell us ....'
		}
	},
	componentWillRecieveProps: function (nextProps) {
		this.setState({
			description: nextProps.description
		});
	},
	handleChange: function(e) {
		e.preventDefault();
		this.setState({description: e.target.value});
	},
	saveHandler: function(e) {
		var section = this.props.section;
		section.description = this.state.description;
		section.uiEditMode = false;
		actions.putSection(section);
	},
	cancelHandler: function(e) {
		e.preventDefault();
		this.setState({description:this.props.section.description});
		actions.disableEditMode(this.props.section.sectionId);
	},
	render: function() {
		var className = classNames(this.props.className + '-edit', 'u-full-width');
		return (
			<div>
				<div className='row'>
					<h4 className='u-pull-left'>{this.props.title}</h4>
					<a className='button u-pull-right' onClick={this.cancelHandler}>Cancel</a>
					<a className='button u-pull-right' onClick={this.saveHandler}>Save</a>
				</div>
				<textarea className={className} 
					placeholder={this.props.placeholder}
					onChange={this.handleChange} value ={this.state.description} >
				</textarea>
			</div>
		);
	}
});

module.exports = FreeformEdit;