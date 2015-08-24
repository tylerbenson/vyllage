var React = require('react');

var AddSectionOption = React.createClass({
	getInitialState: function(){
		return {
			description: this.props.option.description
		};
	},
	handleMouseOver: function() {
		var option = this.props.option;
		if(!option.isMultiple && option.isAlreadyAdded) {
			this.setState({
				description: 'This section is already added in your resumé. Click to edit.'
			});
		}
	},
	handleMouseOut: function() {
		this.setState({
			description: this.props.option.description
		});
	},
	render: function() {
		var option = this.props.option;
		return (
			<div className="option" onClick={this.props.onClick} onMouseOver={this.handleMouseOver} onMouseOut={this.handleMouseOut}>
				<i className={option.icon}></i>
				<h2 className="title">{option.title}</h2>
				<p className="description">
					{this.state.description}
				</p>
			</div>
		);
	}

});

module.exports = AddSectionOption;