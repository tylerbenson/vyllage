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
		var className = classNames(this.props.className + '-edit', 'u-full-width');
		return (
			<div>
				<div className='row'>
				 <div className="twelve columns section-title">
						<p className='u-pull-left section-title'>{this.props.title}</p>
						<div className='u-pull-right'><CancelBtn cancelHandler={this.cancelHandler}/></div>
						<div className='u-pull-right'><SaveBtn saveHandler={this.saveHandler}/></div>
					</div>
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