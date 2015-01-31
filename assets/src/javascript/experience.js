var CompanyMain = React.createClass({displayName: "CompanyMain",	

    render: function() {
		return (
            React.createElement("div", {className: "company main"}, 
                React.createElement("p", {className: "company-name"}, this.props.experienceData.companyName)
            )
		);
    }
});

var CompanyEdit = React.createClass({displayName: "CompanyEdit",   

    render: function() {
        return (
            React.createElement("div", {className: "company edit"}, 
                React.createElement("input", {type: "text", className: "company-name", name: "company-name", value: this.props.experienceData.companyName}), 
                React.createElement("input", {type: "text", className: "industry", name: "industry", value: this.props.experienceData.industry})
            )
        );
    }
});


var CompanyName = React.createClass({displayName: "CompanyName",   

    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(CompanyMain, {experienceData: this.props.experienceData}), 
                React.createElement(CompanyEdit, {experienceData: this.props.experienceData})
            )
        );
    }
});

var CompanyMainDescription = React.createClass({displayName: "CompanyMainDescription",   

    render: function() {
        return (
            React.createElement("div", {className: "company main"}, 
                React.createElement("p", {className: "description"}, 
                    this.props.experienceData.companyDescription
                )
            )
        );
    }
});

var CompanyEditDescription = React.createClass({displayName: "CompanyEditDescription",   

    render: function() {
        return (
            React.createElement("div", {className: "company edit"}, 
                React.createElement("textarea", {className: "description", name: "description", cols: "20", rows: "3"}, this.props.experienceData.companyDescription)
            )
        );
    }
});

var CompanyDescription = React.createClass({displayName: "CompanyDescription",   

    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(CompanyMainDescription, {experienceData: this.props.experienceData}), 
                React.createElement(CompanyEditDescription, {experienceData: this.props.experienceData})
            )
        );
    }
});

var JobMain = React.createClass({displayName: "JobMain",   

    render: function() {
        return (
            React.createElement("div", {className: "job main"}, 
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
            )
        );
    }
});

var JobEdit = React.createClass({displayName: "JobEdit",   

    render: function() {
        return (
             React.createElement("div", {className: "job edit"}, 
                React.createElement("input", {type: "text", className: "title", value: this.props.experienceData.jobTitle}), 
                React.createElement("input", {type: "text", className: "start-date", value: this.props.experienceData.startDate}), 
                React.createElement("input", {type: "text", className: "end-date", value: this.props.experienceData.endDate}), 
                React.createElement("input", {type: "text", className: "location", value: this.props.experienceData.location})
            )
        );
    }
});


var Title = React.createClass({displayName: "Title",   

    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(JobMain, {experienceData: this.props.experienceData}), 
                React.createElement(JobEdit, {experienceData: this.props.experienceData})
            )
        );
    }
});

var DescriptionMain = React.createClass({displayName: "DescriptionMain",   

    render: function() {
        return (
            React.createElement("div", {className: "job main"}, 
                React.createElement("div", {className: "paragraph"}, 
                    React.createElement("p", {className: "description"}, 
                        this.props.experienceData.jobDescription
                    )
                )
            )
        );
    }
});

var DescriptionEdit = React.createClass({displayName: "DescriptionEdit",   

    render: function() {
        return (
            React.createElement("div", {className: "job edit"}, 
                React.createElement("textarea", {className: "description", name: "description", cols: "40", rows: "6"}, this.props.experienceData.jobDescription)
            )
        );
    }
});

var Description = React.createClass({displayName: "Description",   

    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(DescriptionMain, {experienceData: this.props.experienceData}), 
                React.createElement(DescriptionEdit, {experienceData: this.props.experienceData})
            )
        );
    }
});

var ResponsibilitiesMain = React.createClass({displayName: "ResponsibilitiesMain",   

    render: function() {
        return (
            React.createElement("div", {className: "job main"}, 
                React.createElement("div", {className: "responsibilities"}, 
                    this.props.experienceData.responsibilities
                )
            )
        );
    }
});

var ResponsibilitiesEdit = React.createClass({displayName: "ResponsibilitiesEdit",   

    render: function() {
        return (
             React.createElement("div", {className: "job edit"}, 
                React.createElement("textarea", {className: "responsibilities", cols: "40", rows: "6"}, this.props.experienceData.responsibilities)
            )
        );
    }
});

var Responsibilities = React.createClass({displayName: "Responsibilities",   

    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(ResponsibilitiesMain, {experienceData: this.props.experienceData}), 
                React.createElement(ResponsibilitiesEdit, {experienceData: this.props.experienceData})
            )
        );
    }
});

var Buttons = React.createClass({displayName: "Buttons",   

    render: function() {
        return (
            React.createElement("div", {className: "edit"}, 
                React.createElement("button", {className: "save-btn"}, "save"), 
                React.createElement("button", {className: "cancel-btn"}, "cancel")
            )
        );
    }
});

var ArticleContentExperience = React.createClass({displayName: "ArticleContentExperience",   

    render: function() {
        return (
            React.createElement("div", {className: "article-content experience"}, 
                React.createElement(CompanyName, {experienceData: this.props.experienceData}), 
                React.createElement(CompanyDescription, {experienceData: this.props.experienceData}), 
                React.createElement(Title, {experienceData: this.props.experienceData}), 
                React.createElement(Description, {experienceData: this.props.experienceData}), 
                React.createElement(Responsibilities, {experienceData: this.props.experienceData}), 
                React.createElement(Buttons, {experienceData: this.props.experienceData})
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
    "companyDescription": "Blah Blah Blah.",
    "jobTitle": "Manager, Local Accounts",
    "startDate": "September 2010",
    "endDate": "",
    "isCurrent": true,
    "location": "Portland, Oregon",
    "jobDescription": "Blah Blah Blah",
    "responsibilities": "I was in charge of..."
};

React.render(React.createElement(ExperienceCantainer, {experienceData: DATA}), document.getElementById('experience-container'));


