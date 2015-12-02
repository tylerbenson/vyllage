var React = require('react');
var Textarea = require('react-textarea-autosize');

var HighlightInput = React.createClass({
	addHandler: function(e) {
		if(e.which === 13){
			e.preventDefault();
			var ref = this.refs.highlightInput;
			if(ref) {
				var value = ref.getDOMNode().value.trim();
				if(value.length > 0) {
					this.props.onAdd(value);
					ref.getDOMNode().value = "";
				}
				ref.getDOMNode().focus();
			}
		}
	},
	render: function() {
		return (
			<li className="highlight lastOne">
				<Textarea rows="1"
					className="flat"
					ref="highlightInput"
					onKeyPress={this.addHandler}
        	onFocus={this.props.onFocus}
        	onBlur={this.props.onBlur}
					placeholder="Highlight at least 3 notable accomplishments achieved in this position.." />
			</li>
		);
	}
});

module.exports = HighlightInput;
