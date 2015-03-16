var React = require('react');
var OrganizationEdit = require('./edit');
var OrganizationMain = require('./main');

var SectionsStore = require('./store');
var Actions = require('./actions');

var ArticleContent = React.createClass({  

	getInitialState: function() {
		return {organizationData: ''};
	}, 

	cancel: function () {
		this.handleModeChange();
	},

	handleModeChange: function () {
			this.refs.mainContainer.getDOMNode().style.display="block";
			this.refs.editContainer.getDOMNode().style.display="none";
		return false;
	},

	 goToEditMode: function() {
	 		if(!SectionsStore.getDisableState()){
				var data = JSON.parse(JSON.stringify(this.props.organizationData));
				this.setState({organizationData :data});

				this.refs.mainContainer.getDOMNode().style.display="none";
				this.refs.editContainer.getDOMNode().style.display="block";
			}
	},

	render: function() {
		return (
			<div className="article-content">
				<OrganizationMain ref="mainContainer" organizationData={this.props.organizationData} goToEditMode={this.goToEditMode}/>
				<OrganizationEdit ref="editContainer" organizationData={this.props.organizationData} cancel={this.cancel}/>
			</div>
		);
	}
});

module.exports = ArticleContent;

