var React = require('react');
var Reflux = require('reflux');
var ExportAction = require('./exportAction');

module.exports = React.createClass({
	render:function(){
		return(
			<div>{this.props.active} </div>
		);
	}
});