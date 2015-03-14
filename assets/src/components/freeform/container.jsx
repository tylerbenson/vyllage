var React = require('react');
var FreeformMain = require('./main');
var FreeformEdit = require('./edit');
var ButtonsContainer = require('./buttons-container');

var SectionsStore = require('../organization/store');
var Actions = require('../organization/actions');

var FreeformContainer = React.createClass({

	getInitialState: function() {
		return {description: ''};
	},

	updateDescription: function (value) {
		this.state.description = value;
		this.setState({description: this.state.description });
	},

	save: function () {
		Actions.saveSection(this.state.description);
	},

	cancel: function () {
		this.handleModeChange();
	},

	valid: function (){
		return this.state.description !== undefined && 
				this.state.description.length > 0;
	},

	handleModeChange: function () {
		var data = JSON.parse(JSON.stringify(this.props.freeformData.description));
		this.setState({description :data});

		this.refs.goalMain.getDOMNode().style.display="block";
		this.refs.goalEdit.getDOMNode().style.display="none";
		this.refs.buttonContainer.getDOMNode().style.display="none";
		return false;
	},

	goToEditMode: function() {
		if(!SectionsStore.getDisableState()){
			var data = JSON.parse(JSON.stringify(this.props.freeformData.description));
			this.setState({description :data});

			this.refs.goalMain.getDOMNode().style.display="none";
			this.refs.goalEdit.getDOMNode().style.display="block";
			this.refs.buttonContainer.getDOMNode().style.display="block";
		}
	},

	render: function() {
		return (
			<div className="article-content">
				<FreeformMain ref="goalMain" description={this.props.freeformData.description} goToEditMode ={this.goToEditMode} />
				<FreeformEdit ref="goalEdit" description={this.props.freeformData.description} updateDescription={this.updateDescription} />
				<ButtonsContainer ref="buttonContainer" save={this.save} cancel={this.cancel} valid={this.valid()} sectionId={this.props.freeformData.sectionId}/>
			</div>
		);
	}
});

module.exports = FreeformContainer;


