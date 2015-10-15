var Reflux = require('reflux');
var request = require('superagent');
var urlTemplate = require('url-template');
var endpoints = require('../endpoints');
var findindex = require('lodash.findindex');
var FeatureActions = Reflux.createActions(['featureCheck','featureResult','searchForResult']);
window.featureTogglz = [];

FeatureActions.featureCheck.preEmit = function( featureName ){
	var featureNameIndex = findindex(featureTogglz ,{item: featureName});
	if( featureNameIndex == -1 ){
		featureTogglz.push({ item : featureName , result: false });
		var newIndex = findindex(featureTogglz ,{item: featureName});
		var url = urlTemplate
			.parse(endpoints.togglz)
			.expand({feature: featureName });

		request
			.get(url)
			.end(function(err, res) {
				featureTogglz[newIndex].result = res.body;
				FeatureActions.featureResult(featureTogglz);
		});
	}
	FeatureActions.featureResult(featureTogglz);
}

module.exports = FeatureActions;