var React = require('react');
var Textarea = require('react-textarea-autosize');

var HighlightInput = React.createClass({
	addHandler: function() {
		var ref = this.refs.highlightInput;
		if(ref) {
			var value = ref.getDOMNode().value.trim();

			if(value.length > 0) {
				this.props.onAdd(value);
				ref.getDOMNode().value = "";
			}

			ref.getDOMNode().focus();
		}
	},
	render: function() {
		return (
			<li className="highlight">
				<Textarea rows="1" className="flat" ref="highlightInput" placeholder="Highlight at least 3 notable accomplishments achieved in this position.." />
				<button className="flat icon small secondary" onClick={this.addHandler}>
					<i className="ion-android-add-circle"></i>
				</button>
			</li>
		);
	}
});

module.exports = HighlightInput;