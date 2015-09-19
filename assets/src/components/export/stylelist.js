var React = require('react');
var Reflux = require('reflux');
var viewport = require('viewport-dimensions');

var ExportAction = require('./exportAction');
var classnames = require('classnames');
var configs = require('../configs');
var MAX_WIDTH = configs.breakpoints.largePortrait;

var ResumeStyleList = React.createClass({
	render: function(){
		var documentId = window.location.pathname.split('/')[2];
		var THUMB_HEIGHT = 275;
		var THUMB_WIDTH = 212;

		var styleThumbnails = this.props.data.map( function(pdfStyle , index){
			var classes = classnames({
				'pdf-thumb': true,
				'active': this.props.active === pdfStyle
			});

			var url = '/resume/' + documentId +'/file/png' +
				'?style=' + pdfStyle +
				'&height=' + (THUMB_HEIGHT) +
				'&width=' + (THUMB_WIDTH);

			var link = '/resume/'+ documentId +'/file/pdf?style=' + pdfStyle;

			return (
				<a href={link} onClick={this._handleActive.bind(this, pdfStyle )} target="_blank" key={index} className={classes} >
					<img src={url} className="style-thumbnail" />
					{pdfStyle}
				</a>
			);
		}.bind(this));

		var classes = classnames({
			'style-list': true,
			'mobile': viewport.width() < MAX_WIDTH
		});

		return (
			<div className={classes}>
				{styleThumbnails}
			</div>
		);
	},
	_handleActive : function(activeStyle, e){
		if(viewport.width() >= MAX_WIDTH) {
			ExportAction.changeActive(activeStyle);
			e.preventDefault();
		}
	}

});

module.exports = ResumeStyleList;