var React = require('react');
var Textarea = require('react-textarea-autosize');

var Highlight = React.createClass({
	render: function() {
		if(!this.props.text){
      return null;
    }
		return (
			<li className="highlight">
				<Textarea disabled="disabled" rows="1" className="flat" value={this.props.text.replace(/\n{3,}/,'\n\n')} />
				{this.props.uiEditMode ?
					<button className="flat icon small secondary" onClick={this.props.onDelete}>
						<i className="ion-trash-a"></i>
					</button>
				: null}
			</li>
		);
	}

});

module.exports = Highlight;