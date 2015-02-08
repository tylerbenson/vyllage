var OrganizationMain = React.createClass({displayName: "OrganizationMain",   

    render: function() {
        return (
            React.createElement("div", {className: "nonEditable"}, 

                React.createElement("div", {className: "main"}, 
                    React.createElement("p", {className: "organization-name"}, 
                        this.props.organizationData.organizationName
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("div", {className: "paragraph"}, 
                        React.createElement("p", {className: "organization-description"}, 
                            this.props.organizationData.organizationDescription
                        )
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("p", {className: "title"}, 
                        this.props.organizationData.role
                    ), 
                    React.createElement("p", {className: "start-date"}, 
                        this.props.organizationData.startDate
                    ), 
                    React.createElement("p", {className: "end-date"}, 
                        this.props.organizationData.endDate
                    ), 
                    React.createElement("p", {className: "location"}, 
                        this.props.organizationData.location
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("div", {className: "paragraph"}, 
                        React.createElement("p", {className: "role-description"}, 
                            this.props.organizationData.roleDescription
                        )
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("div", {className: "paragraph"}, 
                        React.createElement("p", {className: "highlights"}, 
                            this.props.organizationData.highlights
                        )
                    )
                )

            )
        );
    }
});

var OrganizationName = React.createClass({displayName: "OrganizationName",

    getInitialState: function() {
        return {organizationName:''}; 
    },

    componentDidUpdate: function () {
        this.state.organizationName = this.props.organizationName;
    },

    handleChange: function(event) {
        this.setState({organizationName: event.target.value});

        if (this.props.updateOrganizationName) {
            this.props.updateOrganizationName(event.target.value);
        }
    },

    render: function() {
        var organizationName = this.state.organizationName; 

        return (
            React.createElement("div", {className: "edit"}, 
                React.createElement("input", {type: "text", className: "organization-name", placeholder: "Organization Name", 
                    value: organizationName, onChange: this.handleChange})
            )
        );
    }
});

var OrganizationDescription = React.createClass({displayName: "OrganizationDescription",  

   getInitialState: function() {
        return {organizationDescription:''}; 
    },

    componentDidUpdate: function () {
        this.state.organizationDescription = this.props.organizationDescription;
    },

    handleChange: function(event) {
        this.setState({organizationDescription: event.target.value});

        if (this.props.updateOrganizationDescription) {
            this.props.updateOrganizationDescription(event.target.value);
        }
    },

    render: function() {
        var organizationDescription = this.state.organizationDescription; 

        return (
            React.createElement("div", {className: "edit"}, 
                React.createElement("textarea", {className: "organization-description", placeholder: "Organization Description", 
                    value: organizationDescription, onChange: this.handleChange})
            )
        );
    }
});

var Role = React.createClass({displayName: "Role",

    getInitialState: function() {
        return {role:''}; 
    },

    componentDidUpdate: function () {
        this.state.role = this.props.role;
    },

    handleChange: function(event) {
        this.setState({role: event.target.value});

        if (this.props.updateRole) {
            this.props.updateRole(event.target.value);
        }
    },

    render: function() {
        var role = this.state.role;

        return (
            React.createElement("input", {type: "text", className: "role", placeholder: "Enter your role", 
                value: role, onChange: this.handleChange})
        );
    }
});

var StartDate = React.createClass({displayName: "StartDate",

    getInitialState: function() {
        return {startDate:''}; 
    },

    componentDidUpdate: function () {
        this.state.startDate = this.props.startDate;
    },

    handleChange: function(event) {
        this.setState({startDate: event.target.value});

        if (this.props.updateStartDate) {
            this.props.updateStartDate(event.target.value);
        }
    },

    render: function() {
        var startDate = this.state.startDate;

        return (
            React.createElement("input", {type: "text", className: "start-date", placeholder: "Start Date", 
                 value: startDate, onChange: this.handleChange})
        );
    }
});

var EndDate = React.createClass({displayName: "EndDate",

    getInitialState: function() {
        return {endDate:''}; 
    },

    componentDidUpdate: function () {
        this.state.endDate = this.props.endDate;
    },

    handleChange: function(event) {
        this.setState({endDate: event.target.value});

        if (this.props.updateEndDate) {
            this.props.updateEndDate(event.target.value);
        }
    },

    render: function() {
        var endDate = this.state.endDate;

        return (
            React.createElement("input", {type: "text", className: "end-date", placeholder: "Etart Date", 
            value: endDate, onChange: this.handleChange})
        );
    }
});

var Location = React.createClass({displayName: "Location",

    getInitialState: function() {
        return {location:''}; 
    },

    componentDidUpdate: function () {
        this.state.location = this.props.location;
    },

    handleChange: function(event) {
        this.setState({location: event.target.value});

        if (this.props.updateLocation) {
            this.props.updateLocation(event.target.value);
        }
    },

    render: function() {
        var location = this.state.location;

        return ( 
            React.createElement("input", {type: "text", className: "location", placeholder: "Location", 
            value: location, onChange: this.handleChange})
        );
    }
});


var RoleDescription = React.createClass({displayName: "RoleDescription",

    getInitialState: function() {
        return {roleDescription:''}; 
    },

    componentDidUpdate: function () {
        this.state.roleDescription = this.props.roleDescription;
    },

    handleChange: function(event) {
        this.setState({roleDescription: event.target.value});

        if (this.props.updateroleDescription) {
            this.props.updateroleDescription(event.target.value);
        }
    },

    render: function() {
        var roleDescription = this.state.roleDescription; 

        return (
            React.createElement("div", {className: "edit"}, 
                React.createElement("textarea", {className: "role-description", placeholder: "Role Description", 
                value: roleDescription, onChange: this.handleChange})
            )
        );
    }
});

var Highlights = React.createClass({displayName: "Highlights",

    getInitialState: function() {
        return {highlights:''}; 
    },

    componentDidUpdate: function () {
        this.state.highlights = this.props.highlights;
    },

    handleChange: function(event) {
        this.setState({highlights: event.target.value});

        if (this.props.updateRoleHighlights) {
            this.props.updateRoleHighlights(event.target.value);
        }
    },

    render: function() {
        var highlights = this.state.highlights; 

        return (
             React.createElement("div", {className: "edit"}, 
                React.createElement("textarea", {className: "highlights", placeholder: "Highlights", 
                value: highlights, onChange: this.handleChange})
            )
        );
    }
});


var Buttons = React.createClass({displayName: "Buttons",   

    saveHandler: function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.props.save) {
            this.props.save();
        }
    },    

    cancelHandler: function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.props.cancel) {
            this.props.cancel();
        }
    },

    render: function() {
        return (
            React.createElement("div", {className: "buttons-container"}, 
                React.createElement("button", {className: "save-btn", onClick: this.saveHandler}, "save"), 
                React.createElement("button", {className: "cancel-btn", onClick: this.cancelHandler}, "cancel")
            )
        );
    }
});

var OrganizationEdit = React.createClass({displayName: "OrganizationEdit",   

    getInitialState: function() {
        return {organizationData: ''};
    }, 

    componentDidUpdate: function () {
        this.state.organizationData = this.props.organizationData;
    },

    updateOrganizationName: function (value){
        this.state.organizationData.organizationName = value;
    },

    updateOrganizationDescription: function (value){
        this.state.organizationData.organizationDescription = value;
    },

    updateRole: function (value){
        this.state.organizationData.role = value;
    },

    updateroleDescription: function (value){
        this.state.organizationData.roleDescription = value;
    },

    updateRoleHighlights: function (value){
        this.state.organizationData.highlights = value;
    },

    updateStartDate: function (value){
        this.state.organizationData.startDate = value;
    },

    updateEndDate: function (value){
        this.state.organizationData.endDate = value;
    },

    updateLocation: function (value){
        this.state.organizationData.location = value;
    },

    render: function() {
        return (
            React.createElement("div", {className: "editable"}, 
                React.createElement(OrganizationName, {organizationName: this.props.organizationData.organizationName, updateOrganizationName: this.updateOrganizationName}), 
                React.createElement(OrganizationDescription, {organizationDescription: this.props.organizationData.organizationDescription, updateOrganizationDescription: this.updateOrganizationDescription}), 
                
                React.createElement("div", {className: "edit"}, 
                    React.createElement(Role, {role: this.props.organizationData.role, updateRole: this.updateRole}), 
                    React.createElement(StartDate, {startDate: this.props.organizationData.startDate, updateStartDate: this.updateStartDate}), 
                    React.createElement(EndDate, {endDate: this.props.organizationData.endDate, updateEndDate: this.updateEndDate}), 
                    React.createElement(Location, {location: this.props.organizationData.location, updateLocation: this.updateLocation})
                ), 

                React.createElement(RoleDescription, {roleDescription: this.props.organizationData.roleDescription, updateroleDescription: this.updateroleDescription}), 
                React.createElement(Highlights, {highlights: this.props.organizationData.highlights, updateRoleHighlights: this.updateRoleHighlights})
            )
        );
    }
});

var ArticleContent = React.createClass({displayName: "ArticleContent",  

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

            this.refs.buttonContainer.getDOMNode().style.display="none";

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
            this.refs.buttonContainer.getDOMNode().style.display="block";

            this.state.isMain=false;
        }
    },

    render: function() {
        return (
            React.createElement("div", {className: "article-content", onClick: this.goToEditMode}, 

                React.createElement(OrganizationMain, {ref: "mainContainer", organizationData: this.props.organizationData}), 
                React.createElement(OrganizationEdit, {ref: "editContainer", organizationData: this.props.organizationData, updateOrganizationName: this.updateOrganizationName}), 
                React.createElement(Buttons, {ref: "buttonContainer", save: this.save, cancel: this.cancel})
            )
        );
    }
});

var ArticleControlls = React.createClass({displayName: "ArticleControlls",
    render: function() {
        return (
            React.createElement("div", {className: "article-controll"}, 
                React.createElement("div", {className: "article-controll-btns"}, 
                    React.createElement("div", {className: "u-pull-left"}, 
                        React.createElement("a", {href: "", className: "suggestions"}, "suggestions"), 
                        React.createElement("span", {className: "suggestions-count count"}, "2")
                    ), 
                    React.createElement("div", {className: " u-pull-left"}, 
                        React.createElement("a", {href: "", className: "comments"}, "comments"), 
                        React.createElement("span", {className: "suggestions-count count"}, "3")
                    )
                )
            )
        );
    }
});

var CommentsBlog = React.createClass({displayName: "CommentsBlog",
    render: function() {
        return (
            React.createElement("div", {className: "comments-content"}, 
                React.createElement("div", {className: "comment-list-block"}, 
                    React.createElement("div", {className: "comment-person-info"}, 
                        React.createElement("img", {className: "u-pull-left", src: "images/comment-person.png", width: "40", height: "40"}), 
                        React.createElement("p", {className: "u-pull-left name"}, "Richard Zielke"), 
                        React.createElement("p", {className: "u-pull-left date"}, "2 hrs ago")
                    ), 
                    React.createElement("div", {className: "comment-body"}, 
                        React.createElement("p", {className: ""}, 
                            "I like what you are saying here about your experience with DeVry. You have done an excellent job of outlining your successes."
                        )
                    ), 
                    React.createElement("div", {className: "comment-ctrl"}, 
                        React.createElement("div", {className: "u-pull-right"}, 
                            React.createElement("ul", {className: "comment-ctrl-list"}, 
                                React.createElement("li", {className: "comment-ctrl-btn"}, React.createElement("a", {href: "#"}, "thank")
                                ), 
                                React.createElement("li", {className: "comment-ctrl-btn"}, React.createElement("a", {href: "#"}, "hide")
                                ), 
                                React.createElement("li", {className: "comment-ctrl-btn"}, React.createElement("a", {href: "#"}, "reply")
                                )
                            )
                        )
                    )
                ), 

                React.createElement("div", {className: "comment-divider"})
            )
        );
    }
});

var OrganizationCantainer = React.createClass({displayName: "OrganizationCantainer",

     getInitialState: function() {
        return {organizationData: []};
    },

    componentDidMount : function() {
        // ajax call will go here and fetch the profileData
        this.setState({organizationData: OrganizationData});
    },

    saveChanges: function (data) {
        this.setState({organizationData: data});
        // here ajax call will go to the server, and update the data
    },

    render: function() {
        return (
            React.createElement("div", {className: "twelve columns"}, 
                React.createElement("div", null, 
                    React.createElement("button", {className: "article-btn"}, " ", this.state.organizationData.type, " ")
                ), 
                React.createElement(ArticleContent, {organizationData: this.state.organizationData, saveChanges: this.saveChanges}), 

                React.createElement(ArticleControlls, null), 

                React.createElement(CommentsBlog, null)
            )
        );
    }
});

var OrganizationData = {
   "type": "experience",
    "title": "job experience",
    "sectionId": 124,
    "sectionPosition": 2,
    "state": "shown",
    "organizationName": "DeVry Education Group",
    "organizationDescription": "Blah Blah Blah. Blah Blah Blah ...",
    "role": "Manager, Local Accounts",
    "startDate": "September 2010",
    "endDate": "July 2012",
    "isCurrent": true,
    "location": "Portland, Oregon",
    "roleDescription": "Blah Blah Blah ... Blah Blah Blah ...",
    "highlights": "I was in charge of..."
};

React.render(React.createElement(OrganizationCantainer, null), document.getElementById('experience-container'));


