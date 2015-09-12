var React = require('react');
var Reflux = require('reflux');
var ExportStore = require('./exportStore');
var ExportAction = require('./exportAction');
var ResumeStyleList = require('./stylelist');
var PdfStyleRender = require('./pdfrender');


module.exports = React.createClass({
	mixins: [Reflux.connect(ExportStore)],
	getInitialState: function () {
	    return {
	        'activeStyle' : 'default'  
	    };
	},
	componentDidMount: function () {
		ExportAction.getAllResumeStyle();  
	},
  render: function () {
		var showResumeStyle = '';
		if( this.state.styles != undefined && this.state.styles.length ){
			showResumeStyle = <ResumeStyleList active={this.state.activeStyle} data={this.state.styles} />;
		}
    return (
    	<div>
				<div className="banner">
					<div className="content">
						<i className="header-icon ion-printer"></i>
						<h1>Export</h1>
						<p>Elegant templates for the job that awaits you!</p>
					</div>
				</div>
				<div className="pdfholder">
			    <div className="content">
							<p>Select from our templates below. More to come soon!</p> <br/>
							{showResumeStyle}
			    </div>
		    </div>
		    <PdfStyleRender active={this.state.activeStyle} />
	    </div>
    );
  }

});

