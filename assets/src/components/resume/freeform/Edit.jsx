var React = require('react');
var classNames = require('classnames');
var actions = require('../actions');

var SaveBtn = require('../../buttons/save');
var CancelBtn = require('../../buttons/cancel');

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
		this.setState({description:this.props.section.description});
		actions.disableEditMode(this.props.section.sectionId);
	},
	render: function() {
		return (
			<div>
				<div className='header'>
					<h1>{this.props.title}</h1>
					<div className='pull right actions'>	
						<SaveBtn onClick={this.saveHandler}/>
						<CancelBtn onClick={this.cancelHandler}/>
					</div>
				</div>
				<div className='fields'>
					<textarea 
						placeholder={this.props.placeholder}
						onChange={this.handleChange} value ={this.state.description} >
					</textarea>
				</div>
			</div>
		);
	}
});

module.exports = FreeformEdit;