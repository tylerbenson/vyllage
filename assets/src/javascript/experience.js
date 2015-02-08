var ExperienceMain = React.createClass({displayName: "ExperienceMain",   

    render: function() {
        return (
            React.createElement("div", {className: "nonEditable"}, 

                React.createElement("div", {className: "main"}, 
                    React.createElement("p", {className: "company-name"}, 
                        this.props.experienceData.companyName, 
                        this.props.experienceData.industry
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("div", {className: "paragraph"}, 
                        React.createElement("p", {className: "company-description"}, 
                            this.props.experienceData.companyDescription
                        )
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("p", {className: "title"}, 
                        this.props.experienceData.jobTitle
                    ), 
                    React.createElement("p", {className: "start-date"}, 
                        this.props.experienceData.startDate
                    ), 
                    React.createElement("p", {className: "end-date"}, 
                        this.props.experienceData.endDate
                    ), 
                    React.createElement("p", {className: "location"}, 
                        this.props.experienceData.location
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("div", {className: "paragraph"}, 
                        React.createElement("p", {className: "job-description"}, 
                            this.props.experienceData.jobDescription
                        )
                    )
                ), 

                React.createElement("div", {className: "main"}, 
                    React.createElement("div", {className: "paragraph"}, 
                        React.createElement("p", {className: "responsibilities"}, 
                            this.props.experienceData.responsibilities
                        )
                    )
                )

            )
        );
    }
});


var CompanyName = React.createClass({displayName: "CompanyName",   

    render: function() {
        return (
            React.createElement("div", {className: "edit"}, 
                React.createElement("input", {type: "text", className: "company-name", value: this.props.experienceData.companyName}), 
                React.createElement("input", {type: "text", className: "industry", value: this.props.experienceData.industry})
            )
        );
    }
});

var CompanyDescription = React.createClass({displayName: "CompanyDescription",   

    render: function() {
        return (
            React.createElement("div", {className: "edit"}, 
                React.createElement("textarea", {className: "company-description"}, this.props.experienceData.companyDescription)
            )
        );
    }
});

var Job = React.createClass({displayName: "Job",   

    render: function() {
        return (
             React.createElement("div", {className: "edit"}, 
                React.createElement("input", {type: "text", className: "title", value: this.props.experienceData.jobTitle}), 
                React.createElement("input", {type: "text", className: "start-date", value: this.props.experienceData.startDate}), 
                React.createElement("input", {type: "text", className: "end-date", value: this.props.experienceData.endDate}), 
                React.createElement("input", {type: "text", className: "location", value: this.props.experienceData.location})
            )
        );
    }
});

var JobDescription = React.createClass({displayName: "JobDescription",   

    render: function() {
        return (
            React.createElement("div", {className: "edit"}, 
                React.createElement("textarea", {className: "job-description"}, this.props.experienceData.jobDescription)
            )
        );
    }
});

var Responsibilities = React.createClass({displayName: "Responsibilities",   

    render: function() {
        return (
             React.createElement("div", {className: "edit"}, 
                React.createElement("textarea", {className: "responsibilities"}, this.props.experienceData.responsibilities)
            )
        );
    }
});


var Buttons = React.createClass({displayName: "Buttons",   

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
            React.createElement("div", {className: "edit"}, 
                React.createElement("button", {className: "save-btn", onClick: this.saveHandler}, "save"), 
                React.createElement("button", {className: "cancel-btn", onClick: this.cancelHandler}, "cancel")
            )
        );
    }
});

var ExperienceEdit = React.createClass({displayName: "ExperienceEdit",   

    render: function() {
        return (
            React.createElement("div", {className: "editable"}, 
                React.createElement(CompanyName, {experienceData: this.props.experienceData}), 
                React.createElement(CompanyDescription, {experienceData: this.props.experienceData}), 
                React.createElement(Job, {experienceData: this.props.experienceData}), 
                React.createElement(JobDescription, {experienceData: this.props.experienceData}), 
                React.createElement(Responsibilities, {experienceData: this.props.experienceData})
                
              
            )
        );
    }
});

var ArticleContentExperience = React.createClass({displayName: "ArticleContentExperience",  

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
            var data = JSON.parse( JSON.stringify( this.props.experienceData ));
            this.setState({ experienceData :data,
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
             var data = JSON.parse( JSON.stringify( this.props.experienceData ));
             this.setState({ experienceData :data,
                        isMain: false });

            this.refs.mainContainer.getDOMNode().style.display="none";
            this.refs.editContainer.getDOMNode().style.display="block";
            this.refs.buttonContainer.getDOMNode().style.display="block";

            this.state.isMain=false;
        }
    },

    render: function() {
        return (
            React.createElement("div", {className: "article-content experience", onClick: this.goToEditMode}, 

                React.createElement(ExperienceMain, {ref: "mainContainer", experienceData: this.props.experienceData}), 
                React.createElement(ExperienceEdit, {ref: "editContainer", experienceData: this.props.experienceData}), 
                React.createElement(Buttons, {ref: "buttonContainer", experienceData: this.props.experienceData, save: this.save, cancel: this.cancel})
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

var ExperienceCantainer = React.createClass({displayName: "ExperienceCantainer",   

    render: function() {
        return (
            React.createElement("div", {className: "twelve columns"}, 
                React.createElement("div", null, 
                    React.createElement("button", {className: "article-btn"}, " experience ")
                ), 
                React.createElement(ArticleContentExperience, {experienceData: this.props.experienceData}), 

                React.createElement(ArticleControlls, null), 

                React.createElement(CommentsBlog, null)
            )
        );
    }
});

var DATA = {
    "type": "experience",
    "sectionPosition": 2,
    "companyName": "DeVry1",
    "industry": "Education Group",
    "companyDescription": "Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah.",
    "jobTitle": "Manager, Local Accounts",
    "startDate": "September 2010",
    "endDate": "",
    "isCurrent": true,
    "location": "Portland, Oregon",
    "jobDescription": "Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah",
    "responsibilities": "I was in charge of..."

    // "type": "experience",
    // "title": "job experience",
    // "sectionId": 124,
    // "sectionPosition": 2,
    // "state": "shown",
    // "organizationName": "DeVry Education Group",
    // "organizationDescription": "Blah Blah Blah.",
    // "role": "Manager, Local Accounts",
    // "startDate": "September 2010",
    // "endDate": "",
    // "isCurrent": true,
    // "location": "Portland, Oregon",
    // "roleDescription": "Blah Blah Blah",
    // "highlights": "I was in charge of..."
};

React.render(React.createElement(ExperienceCantainer, {experienceData: DATA}), document.getElementById('experience-container'));


