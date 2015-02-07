// ----------------------- PROFILE SECTION --------------------------


// -------------------------- profile photo container -------------------------- 

var ProfilePhotoContainer = React.createClass({

    render: function() {
        return (
            <div className="four columns">
                <img className="profile-photo" src="images/profile-photo.png" width="115" height="115" />
            </div>
        );
    }
});

// ---------------------------------- end ----------------------------------------------------

// --------------------------headline, tagline container, main mode -------------------------- 
var HeadlineContainerMain = React.createClass({

    render: function() {
        return (
            <div className="headline-container main">
                <div className="paragraph">
                    <p className="headline">
                        {this.props.profileData.headline}
                    </p>
                </div>
                <div className="paragraph">
                    <p className="tagline">{this.props.profileData.tagline}</p>
                </div>
            </div>
        );
    }
});

// ---------------------------------- end ----------------------------------------------------

// ---------------------------headline, tagline container, edit mode -------------------------- 

var HeadlineEdit = React.createClass({  

    getInitialState: function() {
        return {headlineData:''}; 
    },

    componentDidUpdate: function () {
        this.state.headlineData = this.props.profileData.headline;
    },

    handleChange: function(event) {
        this.setState({headlineData: event.target.value});

        if (this.props.changeHeadline) {
            this.props.changeHeadline(event.target.value);
        }
    },

    render: function() {
        var headlineData = this.state.headlineData; 

        return (
            <input type="text" className="headline" placeholder="name, surname" 
                value= {headlineData} onChange={this.handleChange} />         
        );
    }
});

var TaglineEdit = React.createClass({ 

    getInitialState: function() {
        return {taglineData: ''}; 
    },

    componentDidUpdate: function () {
        this.state.taglineData = this.props.profileData.tagline;
    },

    handleChange: function(event) {
        this.setState({taglineData: event.target.value});

        if (this.props.changeTagline) {
            this.props.changeTagline(event.target.value);
        }
    },

    render: function() {
        var taglineData = this.state.taglineData;

        return (
            <input type="text" className="tagline"
                placeholder="add a professional tagline" 
                value = {taglineData} 
                onChange={this.handleChange} />
        );
    }
});

var HeadlineContainerEdit = React.createClass({

    changeHeadline: function (value){
        if (this.props.updateHeadline) {
            this.props.updateHeadline(value);
        }
    },

    changeTagline: function (value){
        if (this.props.updateTagline) {
            this.props.updateTagline(value);
        }
    },

    render: function() {
        return (
            <div className="headline-container edit">
                <HeadlineEdit profileData={this.props.profileData} changeHeadline={this.changeHeadline}/>
                <TaglineEdit profileData={this.props.profileData} changeTagline={this.changeTagline} />
            </div>
        );
    }
});

// --------------------------------------------- end --------------------------------------------


// ---------------------------------- save, cancel buttons container ----------------------------

var ButtonsContainer = React.createClass({  

    saveHandler: function(event) {

        if (this.props.save) {
            this.props.save();
        }

        event.preventDefault();
        event.stopPropagation();
    },    

    cancelHandler: function(event) {

        if (this.props.cancel) {
            this.props.cancel();
        }

        event.preventDefault();
        event.stopPropagation();
    },    

    render: function() {
        return (
            <div className="edit buttons-container">
                <button className="save-btn" onClick={this.saveHandler}>save</button>
                <button className="cancel-btn" onClick={this.cancelHandler}>cancel</button>
            </div>
        );
    }
});

// --------------------------------------------- end --------------------------------------------


// -------------------------------------- Article container  ------------------------------------

var ArticleContent = React.createClass({ 

    getInitialState: function() {
        return {isMain: true,
                 profileData: ''};
    },

    updateHeadline: function (value) {
        this.state.profileData.headline = value;
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
            this.refs.buttonContainer.getDOMNode().style.display="block";

            this.state.isMain=false;
        }
    },

    render: function() {
        return (
            <div className="four columns article-content profile" onClick={this.goToEditMode}>
                <div>
                    <div>
                        <HeadlineContainerMain ref="mainContainer" profileData={this.props.profileData} />
                        <HeadlineContainerEdit ref="editContainer" profileData={this.props.profileData} updateHeadline={this.updateHeadline} updateTagline={this.updateTagline} />
                    </div>

                    <ButtonsContainer ref="buttonContainer"  save={this.save} cancel={this.cancel}/>
                </div>
            </div>
        );
    }
});

// --------------------------------------------- end --------------------------------------------

// -------------------------- Profile Container for both modes ----------------------------------

var ProfileContainer = React.createClass({ 

    getInitialState: function() {
        return {profileData: []};
    },

    componentDidMount: function() {
        // ajax call will go here and fetch the profileData

        // $.ajax({
        //     url: this.props.url,
        //     dataType: 'json',
        //     success: function(data) {
        //         this.setState({profileData: data});
        //     }.bind(this),
        //     error: function(xhr, status, err) {
        //         console.error(this.props.url, status, err.toString());
        //     }.bind(this)
        // });

        this.setState({profileData: Data});
    },

    saveChanges: function (data) {
        this.setState({profileData: data});

        // here ajax call will go to the server, and update the data
    },

    render: function() {
        return (
           <div className="row">
                <ProfilePhotoContainer />

                <ArticleContent profileData={this.state.profileData} saveChanges={this.saveChanges}/>

                <div className="four columns btns-grid">
                    <div className="share-contact-btns-container">
                        <button className="u-pull-left share" id="shareInfoBtn">share</button>
                        <button className="u-pull-left contact" id="contactInfoBtn">contact</button>
                    </div>
                </div>
            </div>
        );
    }

});

// --------------------------------------------- end --------------------------------------------


//   ----------------------------------------- render --------------------------------------------

var Data = { 
    headline: 'Nathan M Benson',
    tagline: 'Technology Enthusiast analyzing, building, and expanding solutions'
};

React.render(<ProfileContainer />, document.getElementById('profile'));


