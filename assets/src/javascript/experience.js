var OrganisationMain = React.createClass({displayName: "OrganisationMain",   

    render: function() {
        return (
            React.createElement("div", {className: "nonEditable"}, 

                React.createElement("div", {className: "main"}, 
                    React.createElement("p", {className: "organization-name"}, 
                        this.props.organisationData.organizationName
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("div", {className: "paragraph"}, 
                        React.createElement("p", {className: "organization-description"}, 
                            this.props.organisationData.organizationDescription
                        )
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("p", {className: "role"}, 
                        this.props.organisationData.role
                    ), 
                    React.createElement("p", {className: "start-date"}, 
                        this.props.organisationData.startDate
                    ), 
                    React.createElement("p", {className: "end-date"}, 
                        this.props.organisationData.endDate
                    ), 
                    React.createElement("p", {className: "location"}, 
                        this.props.organisationData.location
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("div", {className: "paragraph"}, 
                        React.createElement("p", {className: "role-description"}, 
                            this.props.organisationData.roleDescription
                        )
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("div", {className: "paragraph"}, 
                        React.createElement("p", {className: "highlights"}, 
                            this.props.organisationData.highlights
                        )
                    )
                )

            )
        );
    }
});

var OrganizationName = React.createClass({displayName: "OrganizationName",   

    render: function() {
        return (
            React.createElement("div", {className: "edit"}, 
                React.createElement("input", {type: "text", className: "organization-name", value: this.props.organizationName})
            )
        );
    }
});

var OrganizationDescription = React.createClass({displayName: "OrganizationDescription",   

    render: function() {
        return (
            React.createElement("div", {className: "edit"}, 
                React.createElement("textarea", {className: "organization-description", value: this.props.organizationDescription})
            )
        );
    }
});

var Role = React.createClass({displayName: "Role",   

    render: function() {
        return (
             React.createElement("div", {className: "edit"}, 
                React.createElement("input", {type: "text", className: "title", value: this.props.organisationData.role}), 
                React.createElement("input", {type: "text", className: "start-date", value: this.props.organisationData.startDate}), 
                React.createElement("input", {type: "text", className: "end-date", value: this.props.organisationData.endDate}), 
                React.createElement("input", {type: "text", className: "location", value: this.props.organisationData.location})
            )
        );
    }
});

var RoleDescription = React.createClass({displayName: "RoleDescription",   

    render: function() {
        return (
            React.createElement("div", {className: "edit"}, 
                React.createElement("textarea", {className: "role-description", value: this.props.roleDescription})
            )
        );
    }
});

var Highlights = React.createClass({displayName: "Highlights",   

    render: function() {
        return (
             React.createElement("div", {className: "edit"}, 
                React.createElement("textarea", {className: "highlights", value: this.props.highlights})
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
            React.createElement("div", {className: "edit"}, 
                React.createElement("button", {className: "save-btn", onClick: this.saveHandler}, "save"), 
                React.createElement("button", {className: "cancel-btn", onClick: this.cancelHandler}, "cancel")
            )
        );
    }
});

var OrganisationEdit = React.createClass({displayName: "OrganisationEdit",   

    render: function() {
        return (
            React.createElement("div", {className: "editable"}, 
                React.createElement(OrganizationName, {organizationName: this.props.organisationData.organizationName}), 
                React.createElement(OrganizationDescription, {organizationDescription: this.props.organisationData.organizationDescription}), 
                React.createElement(Role, {organisationData: this.props.organisationData}), 
                React.createElement(RoleDescription, {roleDescription: this.props.organisationData.roleDescription}), 
                React.createElement(Highlights, {highlights: this.props.organisationData.highlights})
            )
        );
    }
});

var ArticleContent = React.createClass({displayName: "ArticleContent",  

    getInitialState: function() {
        return {
                isMain: true,
                experienceData: ''
            };
    }, 

    save: function () {
        if(this.props.saveChanges){
            this.props.saveChanges(this.state.experienceData);
        }
        this.handleModeChange();
    },

    cancel: function () {
        this.handleModeChange();
    },

    handleModeChange: function () {

        if(!this.state.isMain) {
            var data = JSON.parse(JSON.stringify(this.props.organisationData));
            this.setState({ 
                organisationData :data,
                isMain: true
            });

            this.refs.mainContainer.getDOMNode().style.display="block";
            this.refs.editContainer.getDOMNode().style.display="none";

            this.refs.buttonContainer.getDOMNode().style.display="none";

            this.state.isMain=true ;
        }

        return false;
    },

     goToEditMode: function() {

        if(this.state.isMain) {
            var data = JSON.parse(JSON.stringify(this.props.organisationData));
            this.setState({
                organisationData :data,
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
            React.createElement("div", {className: "article-content experience", onClick: this.goToEditMode}, 

                React.createElement(OrganisationMain, {ref: "mainContainer", organisationData: this.props.organisationData}), 
                React.createElement(OrganisationEdit, {ref: "editContainer", organisationData: this.props.organisationData}), 
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

var OrganisationCantainer = React.createClass({displayName: "OrganisationCantainer",

     getInitialState: function() {
        return {organisationData: []};
    },

    componentDidMount : function() {
        // ajax call will go here and fetch the profileData
        this.setState({organisationData: OrganisationData});
    },

    saveChanges: function (data) {
        this.setState({organisationData: data});
        // here ajax call will go to the server, and update the data
    },

    render: function() {
        return (
            React.createElement("div", {className: "twelve columns"}, 
                React.createElement("div", null, 
                    React.createElement("button", {className: "article-btn"}, " ", this.state.organisationData.type, " ")
                ), 
                React.createElement(ArticleContent, {organisationData: this.state.organisationData}), 

                React.createElement(ArticleControlls, null), 

                React.createElement(CommentsBlog, null)
            )
        );
    }
});

var OrganisationData = {
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

React.render(React.createElement(OrganisationCantainer, null), document.getElementById('experience-container'));


