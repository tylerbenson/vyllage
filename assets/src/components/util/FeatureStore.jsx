var Reflux = require('reflux');

module.exports = Reflux.createStore({
	listenables: require('./FeatureActions'),
	onFeatureResult : function(featureTogglz){
		this.trigger( featureTogglz );
	}
});