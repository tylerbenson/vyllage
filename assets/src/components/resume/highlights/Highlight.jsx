var React = require('react');
var Textarea = require('react-textarea-autosize');
var ContentEditable = require("react-contenteditable");

var Highlight = React.createClass({
	render: function() {
		if(!this.props.text){
      return null;
    }
		return (
			<li className="highlight flat">
				<ContentEditable html={this.props.text.replace(/\n{3,}/,'\n\n')} onChange={this._handleEdit}  />
			</li>
		);
	},

	_handleEdit : function(e){
		
	}

});

module.exports = Highlight;