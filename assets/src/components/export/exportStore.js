var Reflux = require('reflux');

var ExportStore = Reflux.createStore({
	listenables: require('./exportAction'),
	onResultForOwner:function(owner){
		if( owner != true ){
			window.location.replace('/');
		}
	},
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