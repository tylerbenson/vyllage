var React = require('react');
var Reflux = require('reflux');
var ExportAction = require('./exportAction');

module.exports = React.createClass({
	render:function(){
		var documentId = window.location.pathname.split('/')[2];
		var pdfLink = window.location.origin + '/resume/'+ documentId +'/file/pdf?style=' + this.props.active;
		return(
			<div className="live-preview">
		    	<object data={pdfLink} type="application/pdf" className="pdfrender"></object>
			</div>
		);
	}
});