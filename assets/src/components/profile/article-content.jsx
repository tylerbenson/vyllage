var React = require('react');

var HeadlineContainer = require('./headline');
var TaglineContainer = require('./tagline');
var TaglineEdit = require('./tagline-edit');
var ButtonsContainer =  require('./buttons');

var ArticleContent = React.createClass({ 

    getInitialState: function() {
        return {isMain: true,
                 profileData: ''};
    },

    updateTagline: function (value) {
        this.state.profileData.tagline = value;
    },

    save: function () {
        if(this.props.saveChanges){
            this.props.saveChanges(this.state.profileData);
        }
      this.handleModeChange();
    },

    cancel: function () {
        this.handleModeChange();
    },

    handleModeChange: function () {

        if(!this.state.isMain) {
            var data = JSON.parse( JSON.stringify( this.props.profileData ));
            this.setState({ profileData :data,
                            isMain: true });

            this.refs.mainContainer.getDOMNode().style.display="block";
            this.refs.editContainer.getDOMNode().style.display="none";

            this.refs.buttonContainer.getDOMNode().style.display="none";

            this.state.isMain=true ;
        }

        return false;
    },

    goToEditMode: function() {

        if(this.state.isMain) {
             var data = JSON.parse( JSON.stringify( this.props.profileData ));
             this.setState({ profileData :data,
                        isMain: false });

            this.refs.mainContainer.getDOMNode().style.display="none";
            this.refs.editContainer.getDOMNode().style.display="block";
            this.refs.buttonContainer.getDOMNode().style.display="block" ;

            this.state.isMain=false;
        }
    },

    render: function() {
        return (
            <div className="four columns article-content profile">
                <HeadlineContainer profileData={this.props.profileData} />
                <div onClick={this.goToEditMode}>
                    <TaglineContainer ref="mainContainer" profileData={this.props.profileData} />
                    <div ref="editContainer" className="editMode">
                        <TaglineEdit profileData={this.props.profileData} updateTagline={this.updateTagline} />
                        <ButtonsContainer ref="buttonContainer"  save={this.save} cancel={this.cancel}/>
                    </div>
                </div>
                
            </div>
        );
    }
});

module.exports = ArticleContent;