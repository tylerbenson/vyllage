var React = require('react');
var Textarea = require('react-textarea-autosize');

var Highlight = React.createClass({
	render: function() {
		if(!this.props.text){
      return null;
    }
		return (
			<li className="highlight">
				<span className="flat">{this.props.text.replace(/\n{3,}/,'\n\n')}</span>
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