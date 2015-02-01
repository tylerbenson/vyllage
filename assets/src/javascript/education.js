var EstablishmentMain = React.createClass({displayName: "EstablishmentMain",	

    render: function() {
		return (
            React.createElement("div", {className: "education main"}, 
                React.createElement("p", {className: "establishment"}, 
                    this.props.educationData.schoolName
                )
            )
		);
    }
});

var EstablishmentEdit = React.createClass({displayName: "EstablishmentEdit",   

    render: function() {
        return (
            React.createElement("div", {className: "education edit"}, 
                React.createElement("input", {type: "text", className: "description_edu", name: "job-title", value: this.props.educationData.schoolName})
            )
        );
    }
});


var Establishment = React.createClass({displayName: "Establishment",   
    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(EstablishmentMain, {educationData: this.props.educationData}), 
                React.createElement(EstablishmentEdit, {educationData: this.props.educationData})
            )
        );
    }
});

var DescriptionMain = React.createClass({displayName: "DescriptionMain", 

    render: function() {
        return (
            React.createElement("div", {className: "education main"}, 
                React.createElement("div", {className: "paragraph"}, 
                    React.createElement("p", {className: "description"}, 
                        this.props.educationData.schoolDescription
                    )
                )
            )
        );
    }
});

var DescriptionEdit = React.createClass({displayName: "DescriptionEdit", 

    render: function() {
        return (
            React.createElement("div", {className: "education edit"}, 
                React.createElement("textarea", {className: "description", name: "description"}, this.props.educationData.schoolDescription)
            )
        );
    }
});


var Description = React.createClass({displayName: "Description",   
    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(DescriptionMain, {educationData: this.props.educationData}), 
                React.createElement(DescriptionEdit, {educationData: this.props.educationData})
            )
        );
    }
});


var TitleMain = React.createClass({displayName: "TitleMain",   

    render: function() {
        return (
            React.createElement("div", {className: "education main"}, 
                React.createElement("p", {className: "title"}, 
                     this.props.educationData.degree
                ), 
                React.createElement("p", {className: "start-date"}, 
                    this.props.educationData.startDate
                ), 
                React.createElement("p", {className: "end-date"}, 
                    this.props.educationData.endDate
                ), 
                React.createElement("p", {className: "location"}, 
                    this.props.educationData.location
                )
            )
        );
    }
});

var TitleEdit = React.createClass({displayName: "TitleEdit",   

    render: function() {
        return (
            React.createElement("div", {className: "education edit"}, 
                React.createElement("input", {type: "text", className: "title", value: this.props.educationData.degree}), 
                React.createElement("input", {type: "text", className: "start-date", value: this.props.educationData.startDate}), 
                React.createElement("input", {type: "text", className: "end-date", value: this.props.educationData.endDate}), 
                React.createElement("input", {type: "text", className: "location", value: this.props.educationData.location})
            )
        );
    }
});

var Title = React.createClass({displayName: "Title",   
    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(TitleMain, {educationData: this.props.educationData}), 
                React.createElement(TitleEdit, {educationData: this.props.educationData})
            )
        );
    }
});

var GpaMain = React.createClass({displayName: "GpaMain",   

    render: function() {
        return (
            React.createElement("div", {className: "education main"}, 
                React.createElement("p", {className: "gpa"}, " ", this.props.educationData.highlights, " ")
            )
        );
    }
});

var GpaEdit = React.createClass({displayName: "GpaEdit",   

    render: function() {
        return (
            React.createElement("div", {className: "education edit"}, 
                React.createElement("input", {type: "text", className: "gpa", name: "gpa", value: this.props.educationData.highlights})
            )
        );
    }
});

var Gpa = React.createClass({displayName: "Gpa",   
    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(GpaMain, {educationData: this.props.educationData}), 
                React.createElement(GpaEdit, {educationData: this.props.educationData})
            )
        );
    }
});


var Education = React.createClass({displayName: "Education",   

    render: function() {
        return (
             React.createElement("div", {className: "article-content education"}, 
                React.createElement(Establishment, {educationData: this.props.educationData}), 
                React.createElement(Description, {educationData: this.props.educationData}), 
                React.createElement(Title, {educationData: this.props.educationData}), 
                React.createElement(Gpa, {educationData: this.props.educationData}), 
                React.createElement("div", {className: "edit"}, 
                    React.createElement("button", {className: "save-btn"}, "save"), 
                    React.createElement("button", {className: "cancel-btn"}, "cancel")
                )
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

var EducationCantainer = React.createClass({displayName: "EducationCantainer",   

    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement("div", null, 
                    React.createElement("button", {className: "article-btn"}, " ", this.props.educationData.type, " ")
                ), 
                React.createElement(Education, {educationData: this.props.educationData}), 

                React.createElement(ArticleControlls, null), 

                React.createElement(CommentsBlog, null)
            )
        );
    }
});


var EducationData = {
    "type": "education",
    "sectionPosition": 3,
    "schoolName": "Keller Graduate School of Management new",
    "schoolDescription": "Blah Blah Blah.",
    "degree": "Masters of Project Management",
    "startDate": "September 2010",
    "endDate": "September 2012",
    "isCurrent": false,
    "location": "Portland, Oregon",
    "highlights": "GPA 3.84, Summa Cum Laude, Awesome Senior Project"
};

React.render(React.createElement(EducationCantainer, {educationData: EducationData}), document.getElementById('education-cantainer'));


