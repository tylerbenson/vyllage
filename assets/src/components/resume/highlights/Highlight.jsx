var React = require('react');

var Highlight = React.createClass({
	render: function() {
		return (
			<li className="highlight">
				{this.props.uiEditMode ?
				<input className="flat highlightInput"
					onChange={this._handleEdit}
					onKeyDown={this._handlePress}
        	onFocus={this.props.onFocus}
        	onBlur={this.props.onBlur}
					value={this.props.text ? this.props.text.replace(/\n{3,}/,'\n\n') : ''} />
				: <span className="flat">{this.props.text.replace(/\n{3,}/,'\n\n')}</span> }
			</li>
		);
	},
	_handlePress : function(e){
		if(e.keyCode === 13 ){
			 jQuery(e.target).blur();
			 jQuery(React.findDOMNode(this)).siblings('.lastOne').find('textarea').focus();
		}
		if( e.target.value == ''){
			if( e.keyCode === 8)
				this.props.onDelete();
				jQuery(React.findDOMNode(this)).siblings('.lastOne').find('textarea').focus();
		}
	},
	_handleEdit : function(e){
		this.props.onEdit(e.target.value);
	}
});

module.exports = Highlight;
