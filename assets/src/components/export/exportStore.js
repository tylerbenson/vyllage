var Reflux = require('reflux');

var ExportStore = Reflux.createStore({
	listenables: require('./exportAction'),
	onGrabResumeStyle:function(styles){
		this.styles = styles;
		this.update();
	},
	onChangeActive:function( activeStyle ){
		this.activeStyle = activeStyle;
		this.update();
	},
	update:function(){
		this.trigger(this);
	}
});

module.exports = ExportStore;