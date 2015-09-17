var React = require('react');
var Reflux = require('reflux');
var ExportAction = require('./exportAction');

var ResumeStyleList = React.createClass({
	render: function(){
		var self = this;
		var documentId = window.location.pathname.split('/')[2];
		var resumeStyle = function(pdfstyle , index ){
			var ActiveClass='pdf-thumb';
			if( self.props.active == pdfstyle ){
				ActiveClass = 'active pdf-thumb';
			}
			var url = '/resume/' + documentId +'/file/png?style='+pdfstyle;
			return <div onClick={self._handleActive.bind(self, pdfstyle )} key={index} className={ActiveClass} >
				<img src={url} className="pdfStyleScrsht" />
				{pdfstyle}
			</div>;
		}
		return (
			<div>
				{ this.props.data.map(resumeStyle) }
			</div>
		);
	},
	_handleActive : function(activeStyle){
		 ExportAction.changeActive(activeStyle);
	}

});

module.exports = ResumeStyleList;