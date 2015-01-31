var GoalMain = React.createClass({displayName: "GoalMain",	

    render: function() {
		return (
            React.createElement("div", {className: "goal main"}, 
                React.createElement("div", {className: "paragraph"}, 
                    React.createElement("p", {className: "goal-description"}, 
                       this.props.careerData.description, ";"
                    )
                )
            )
		);
    }
});

var GoalEdit = React.createClass({displayName: "GoalEdit", 

    render: function() {
        return (
            React.createElement("div", {className: "goal edit"}, 
                React.createElement("textarea", {className: "goal-description", 
                            placeholder: "tell us about your career goal...", 
                            cols: "40", rows: "6"}, 
                    this.props.careerData.description
                )
            )
        );
    }
});

var GoalContainer = React.createClass({displayName: "GoalContainer",
    render: function() {
        return (
            React.createElement("div", {className: "article-content career-goal"}, 
                React.createElement("div", null, 
                    React.createElement(GoalMain, {careerData: this.props.careerData}), 
                    React.createElement(GoalEdit, {careerData: this.props.careerData})
                ), 
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



var CareerGoalContainer = React.createClass({displayName: "CareerGoalContainer",
    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement("div", null, 
                    React.createElement("button", {className: "article-btn"}, " ", this.props.careerData.title, " ")
                ), 

                React.createElement(GoalContainer, {careerData: this.props.careerData}), 

                React.createElement(ArticleControlls, null), 

                React.createElement(CommentsBlog, null)
            )
        );
    }
});

var CareerGoalData = {
    "type": "freeform",
    "title": "career goal",
    "sectionPosition": 1,
    "description": " I am a fervid promoter of creating solutions. The rush of working with a team to launch a project that solves critical problems and intersects with technology is extremely satisfying. I have a successful track record, in leading projects, growing new."
};

React.render(React.createElement(CareerGoalContainer, {careerData: CareerGoalData}), document.getElementById('career-goal-container'));


