var React = require('react');
var TagSuggestions = require('../../tags');
require('jquery-ui/autocomplete');

var TagInput = React.createClass({
	componentDidMount: function() {
		var self = this;
		this.refs.tagInput.getDOMNode().focus();
		jQuery( this.refs.tagInput.getDOMNode() ).autocomplete({
      source: this.props.type == 'SkillsSection' ? TagSuggestions.skills : TagSuggestions.careerInterest ,
			select: function( event, ui ) {
				jQuery(ui.item).remove();
			},
			appendTo: ".dummy",
			select: function( event, ui ) {
				self.props.onSelect(ui.item.value);
				ui.item.value = "";
	    }
    });
	},
	render: function() {
		return (
			<div>
				<input className={this.props.className} ref="tagInput" placeholder="Type to add.." type="text" onKeyPress={this.props.onKeyPress} />
			</div>
		);
	}
});

module.exports = TagInput;
