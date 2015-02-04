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
                        { this.props.profileData.firstName + " "
                          + this.props.profileData.middleName + " "
                          + this.props.profileData.lastName
                        }
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
        this.setState({headlineData: this.props.profileData.firstName});
    },

    handleChange: function(event) {
        this.setState({headlineData: event.target.value});

        if (this.props.onChange) {
            this.props.onChange( event.target.value, true);
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
        this.setState({taglineData: this.props.profileData.tagline});
    },

    handleChange: function(event) {
        this.setState({taglineData: event.target.value});

        if (this.props.onChange) {
            this.props.onChange(event.target.value, false);
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

    handleChange: function (tagline, isHeadline){
        if (this.props.onChange) {
            this.props.onChange(tagline, isHeadline);
        }
    },

    render: function() {
        return (
            <div className="headline-container edit">
                <HeadlineEdit profileData={this.props.profileData} onChange={this.handleChange }/>
                <TaglineEdit profileData={this.props.profileData} onChange={this.handleChange } />
            </div>
        );
    }
});

// --------------------------------------------- end --------------------------------------------


// ---------------------------------- save, cancel buttons container ----------------------------

var ButtonsContainer = React.createClass({  

    saveHandler: function(event) {
       if (this.props.saveHandler) {
            this.props.saveHandler(true);
        }
    },    

    cancelHandler: function(event) {
        if (this.props.saveHandler) {
            this.props.saveHandler(false);
        }
    },    

    render: function() {
        return (
            <div className="edit">
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
        return { mainMode: true };
    },

    saveHandler: function (save) {
        this.setState({mainMode: true});
       
        this.refs.mainContainer.getDOMNode().style.display="block";
        this.refs.editContainer.getDOMNode().style.display="none";

        this.refs.buttonContainer.getDOMNode().style.display="none";
    },

    changeMode: function() {

        if(this.state.mainMode) {

            this.refs.mainContainer.getDOMNode().style.display="none";
            this.refs.editContainer.getDOMNode().style.display="block";

            this.refs.buttonContainer.getDOMNode().style.display="block";

            this.setState({mainMode: false});
        }

        return;
    },

    render: function() {
        return (
            <div className="four columns article-content profile" onClick={this.changeMode}>
                <div>
                    <div>
                        <HeadlineContainerMain ref="mainContainer" profileData={this.props.profileData} />
                        <HeadlineContainerEdit ref="editContainer" profileData={this.props.profileData} onChange={this.handleChange}/>
                    </div>

                    <ButtonsContainer ref="buttonContainer"  saveHandler={this.saveHandler }/>
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

    handleChange: function (data, isHeadline) {

        if(isHeadline) {
            this.state.profileData.firstName = data;
        } else {
            this.state.profileData.tagline = data;
        }

        this.setState({profileData: this.state.profileData});
    },

    render: function() {
        return (
           <div className="row">

                <ProfilePhotoContainer />

                <ArticleContent profileData={this.state.profileData}/>

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
    firstName: 'Nathan ',
    middleName: 'M ',
    lastName: 'Benson ',
    tagline: 'Technology Enthusiast analyzing, building, and expanding solutions'
};

React.render(<ProfileContainer />, document.getElementById('profile'));


