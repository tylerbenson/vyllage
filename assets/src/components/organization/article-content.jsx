var React = require('react');
var OrganizationEdit = require('./edit');
var OrganizationMain = require('./main');

var ArticleContent = React.createClass({  

    getInitialState: function() {
        return {
                isMain: true,
                organizationData: ''
            };
    }, 

    save: function () {
        if(this.props.saveChanges){
            this.props.saveChanges(this.refs.editContainer.state.organizationData);
        }
        this.handleModeChange();
    },

    cancel: function () {
        this.handleModeChange();
    },

    handleModeChange: function () {

        if(!this.state.isMain) {
            
            this.refs.mainContainer.getDOMNode().style.display="block";
            this.refs.editContainer.getDOMNode().style.display="none";

            this.state.isMain=true ;
        }

        return false;
    },

     goToEditMode: function() {

        if(this.state.isMain) {
            var data = JSON.parse(JSON.stringify(this.props.organizationData));
            this.setState({
                organizationData :data,
                isMain: false
             });

            this.refs.mainContainer.getDOMNode().style.display="none";
            this.refs.editContainer.getDOMNode().style.display="block";
            this.state.isMain=false;
        }
    },

    render: function() {
        return (
            <div className="article-content" onClick={this.goToEditMode}>

                <OrganizationMain ref="mainContainer" organizationData={this.props.organizationData}/>
                <OrganizationEdit ref="editContainer" organizationData={this.props.organizationData} save={this.save} cancel={this.cancel}/>
                
            </div>
        );
    }
});

module.exports = ArticleContent;

