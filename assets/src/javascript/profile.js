// ----------------------- PROFILE SECTION --------------------------


// -------------------------- profile photo container -------------------------- 

var ProfilePhotoContainer = React.createClass({displayName: "ProfilePhotoContainer",

    render: function() {
        return (
            React.createElement("div", {className: "four columns"}, 
                React.createElement("img", {className: "profile-photo", src: "images/profile-photo.png", width: "115", height: "115"})
            )
        );
    }
});

// ---------------------------------- end ----------------------------------------------------

// --------------------------headline, tagline container, main mode -------------------------- 
var HeadlineContainerMain = React.createClass({displayName: "HeadlineContainerMain",

    render: function() {
        return (
            React.createElement("div", {className: "headline-container main"}, 
                React.createElement("div", {className: "paragraph"}, 
                    React.createElement("p", {className: "headline"}, 
                         this.props.profileData.firstName + " "
                          + this.props.profileData.middleName + " "
                          + this.props.profileData.lastName
                        
                    )
                ), 
                React.createElement("div", {className: "paragraph"}, 
                    React.createElement("p", {className: "tagline"}, this.props.profileData.tagline)
                )
            )
        );
    }
});

// ---------------------------------- end ----------------------------------------------------

// ---------------------------headline, tagline container, edit mode -------------------------- 

var HeadlineEdit = React.createClass({displayName: "HeadlineEdit",  

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
            React.createElement("input", {type: "text", className: "headline", placeholder: "name, surname", 
                value: headlineData, onChange: this.handleChange})         
        );
    }
});

var TaglineEdit = React.createClass({displayName: "TaglineEdit", 

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
            React.createElement("input", {type: "text", className: "tagline", 
                placeholder: "add a professional tagline", 
                value: taglineData, 
                onChange: this.handleChange})
        );
    }
});

var HeadlineContainerEdit = React.createClass({displayName: "HeadlineContainerEdit",  

    handleChange: function (tagline, isHeadline){
        if (this.props.onChange) {
            this.props.onChange(tagline, isHeadline);
        }
    },

    render: function() {
        return (
            React.createElement("div", {className: "headline-container edit"}, 
                React.createElement(HeadlineEdit, {profileData: this.props.profileData, onChange: this.handleChange}), 
                React.createElement(TaglineEdit, {profileData: this.props.profileData, onChange: this.handleChange})
            )
        );
    }
});

// --------------------------------------------- end --------------------------------------------


// ---------------------------------- save, cancel buttons container ----------------------------

var ButtonsContainer = React.createClass({displayName: "ButtonsContainer",  

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
            React.createElement("div", {className: "edit"}, 
                React.createElement("button", {className: "save-btn", onClick: this.saveHandler}, "save"), 
                React.createElement("button", {className: "cancel-btn", onClick: this.cancelHandler}, "cancel")
            )
        );
    }
});

// --------------------------------------------- end --------------------------------------------


// -------------------------------------- Article container  ------------------------------------

var ArticleContent = React.createClass({displayName: "ArticleContent", 

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
            React.createElement("div", {className: "four columns article-content profile", onClick: this.changeMode}, 
                React.createElement("div", null, 
                    React.createElement("div", null, 
                        React.createElement(HeadlineContainerMain, {ref: "mainContainer", profileData: this.props.profileData}), 
                        React.createElement(HeadlineContainerEdit, {ref: "editContainer", profileData: this.props.profileData, onChange: this.handleChange})
                    ), 

                    React.createElement(ButtonsContainer, {ref: "buttonContainer", saveHandler: this.saveHandler})
                )
            )
        );
    }

});

// --------------------------------------------- end --------------------------------------------

// -------------------------- Profile Container for both modes ----------------------------------

var ProfileContainer = React.createClass({displayName: "ProfileContainer", 

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
           React.createElement("div", {className: "row"}, 

                React.createElement(ProfilePhotoContainer, null), 

                React.createElement(ArticleContent, {profileData: this.state.profileData}), 

                React.createElement("div", {className: "four columns btns-grid"}, 
                    React.createElement("div", {className: "share-contact-btns-container"}, 
                        React.createElement("button", {className: "u-pull-left share", id: "shareInfoBtn"}, "share"), 
                        React.createElement("button", {className: "u-pull-left contact", id: "contactInfoBtn"}, "contact")
                    )
                )

            )
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

React.render(React.createElement(ProfileContainer, null), document.getElementById('profile'));


