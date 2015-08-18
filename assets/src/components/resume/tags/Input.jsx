var React = require('react');

var TagInput = React.createClass({
	componentDidMount: function() {
		this.refs.tagInput.getDOMNode().focus();
	},
	render: function() {
		return (
			<input className="inline flat" ref="tagInput" placeholder="Type to add.." type="text" onKeyPress={this.props.onKeyPress} />
		);
	}
});

module.exports = TagInput;