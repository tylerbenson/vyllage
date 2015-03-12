var React = require('react');
var FreeformMain = require('./main');
var FreeformEdit = require('./edit');
var ButtonsContainer = require('./buttons-container');
var Actions = require('../organization/actions');

var FreeformContainer = React.createClass({

    getInitialState: function() {
        return {
            isMain: true,
            freeformData: ''
        };
    },

    updateDescription: function (value) {
        this.state.freeformData.description = value;
        this.setState({freeformData: this.state.freeformData });
    },

    save: function () {
        Actions.saveSection(this.state.freeformData);
        this.handleModeChange();
    },

    cancel: function () {
        this.handleModeChange();
    },

    valid: function (){
        return this.state.freeformData.description !== undefined && 
                this.state.freeformData.description.length > 0;
    },

    handleModeChange: function () {

        if(!this.state.isMain) {
            var data = JSON.parse(JSON.stringify(this.props.freeformData));

            this.setState({ 
                freeformData :data,
                isMain: true 
            });

            this.refs.goalMain.getDOMNode().style.display="block";
            this.refs.goalEdit.getDOMNode().style.display="none";
            this.refs.buttonContainer.getDOMNode().style.display="none";
            this.state.isMain=true ;
        }
        return false;
    },

    goToEditMode: function() {

        if(this.state.isMain) {
            var data = JSON.parse(JSON.stringify(this.props.freeformData));
            this.setState({ 
                freeformData :data,
                isMain: false 
            });

            this.refs.goalMain.getDOMNode().style.display="none";
            this.refs.goalEdit.getDOMNode().style.display="block";
            this.refs.buttonContainer.getDOMNode().style.display="block";
            this.state.isMain=false;
        }
    },

    render: function() {
        return (
            <div className="article-content" onClick={this.goToEditMode}>

                <FreeformMain ref="goalMain" description={this.props.freeformData.description}/>
                <FreeformEdit ref="goalEdit" description={this.props.freeformData.description} updateDescription={this.updateDescription} />

                <ButtonsContainer ref="buttonContainer" save={this.save} cancel={this.cancel} valid={this.valid()}/>
            </div>
        );
    }
});

module.exports = FreeformContainer;


