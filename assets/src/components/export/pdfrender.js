var React = require('react');
var Reflux = require('reflux');
var ExportAction = require('./exportAction');

module.exports = React.createClass({
	render:function(){
		var pdfLink = 'http://localhost:8080/resume/0/file/pdf?style=' + this.props.active;
		return(
			<div>
		    	<object data={pdfLink} type="application/pdf" className="pdfrender"></object>
			</div>
		);
	}
});