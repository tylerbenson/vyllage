var React = require('react');
window.jQuery = require('jquery');
require('jquery-ui/sortable');
require('jquery-ui-touch-punch');

var Sortable = React.createClass({
	componentDidMount: function () {
		var config = this.props.config;
		jQuery(config.list).sortable({
			axis : config.axis ? config.axis : '',
			placeholder: "ui-sortable-placeholder",
			opacity: 0.95,
			scrollSpeed: 15,
			scrollSensitivity: 20,
			forcePlaceholderSize: true,
			cursor: "move",
			items: config.items,
			handle: config.handle,
			start: config.start,
			stop: config.stop
		});
	},
	render: function() {
		return (
			<div {...this.props} />
		);
	}

});

module.exports = Sortable;