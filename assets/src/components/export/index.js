var React = require('react');
var Reflux = require('reflux');
var viewport = require('viewport-dimensions');

var ExportStore = require('./exportStore');
var ExportAction = require('./exportAction');
var ResumeStyleList = require('./stylelist');
var OnResize = require('react-window-mixins').OnResize;
var configs = require('../configs');
var MAX_WIDTH = configs.breakpoints.largePortrait;

module.exports = React.createClass({
	mixins: [Reflux.connect(ExportStore), OnResize],
	getInitialState: function () {
    return {
      'activeStyle' : 'default',
      'viewportWidth' : viewport.width()
    };
	},
	componentWillMount : function () {
	  ExportAction.checkForOwner();
	},
	componentDidMount: function () {
		ExportAction.getAllResumeStyle();
	},
	shouldComponentUpdate: function(nextProps, nextState) {
		return true;
	},
	onResize: function() {
		this.setState({
			'viewportWidth': viewport.width()
		});
  },
  render: function () {
		var styleList = '';
		if( this.state.styles != undefined && this.state.styles.length ){
			styleList = <ResumeStyleList active={this.state.activeStyle} data={this.state.styles} />;
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
				<div className="style-list-container">
					<div className="content">
						{styleList}
			    </div>
		    </div>
	    </div>
    );
  }

});

