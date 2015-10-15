var React = require('react/addons');
var Reflux = require('reflux');
var FeatureActions = require('./FeatureActions');
var FeatureStore = require('./FeatureStore');
var findindex = require('lodash.findindex');

var FeatureToggle = React.createClass({
	mixins: [Reflux.listenTo( FeatureStore,"onStatusChange")],
	getInitialState: function(){
		return {
			isActive: false
		}
	},
	componentWillMount: function() {
		FeatureActions.featureCheck( this.props.name );
	},
	render: function() {
		return (
			<span>
			{this.state.isActive? this.props.children : null}
			</span>
			);
	},
	onStatusChange : function( featureTogglz ){
		var featureNameIndex = findindex(featureTogglz ,{item: this.props.name});
		if( featureNameIndex != -1 ){
			this.setState({isActive : featureTogglz[featureNameIndex].result });
		}
	}
});

module.exports = FeatureToggle;