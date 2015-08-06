var React = require('react');

var AddSectionOption = React.createClass({
	render: function() {
		var option = this.props.option;
		return (
			<div className="option" onClick={this.props.onClick}>
				<i className={option.icon}></i>
				<h2 className="title">{option.title}</h2>
				<p className="description">{option.description}</p>
			</div>
		);
	}

});

module.exports = AddSectionOption;