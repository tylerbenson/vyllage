var FreeformMain = React.createClass({displayName: "FreeformMain",  

    render: function() {
        return (
            React.createElement("div", {className: "nonEditable"}, 

                React.createElement("div", {className: "main"}, 
                    React.createElement("div", {className: "paragraph"}, 
                        React.createElement("p", {className: "freeform-description"}, 
                           this.props.description, ";"
                        )
                    )
                )
            )
        );
    }
});

var  FreeformEdit = React.createClass({displayName: "FreeformEdit", 

    getInitialState: function() {
        return {description:this.props.description}; 
    },

    componentDidUpdate: function () {
        this.state.description = this.props.description;
    },

    handleChange: function(event) {
        this.setState({description: event.target.value});

        if (this.props.updateDescription) {
            this.props.updateDescription(event.target.value);
        }
    },

    render: function() {

        var description = this.state.description;

        return (
            React.createElement("div", {className: "editable"}, 
                React.createElement("div", {className: "edit"}, 
                    React.createElement("textarea", {className: "freeform-description", 
                        placeholder: "tell us about your career goal...", 
                        onChange: this.handleChange, value: description}
                    )
                )
            )
        );
    }
});

var ButtonsContainer = React.createClass({displayName: "ButtonsContainer",  

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

var FreeformContainer = React.createClass({displayName: "FreeformContainer",

    getInitialState: function() {
        return {
            isMain: true,
            freeformData: ''
        };
    },

    updateDescription: function (value) {
        this.state.freeformData.description = value;
    },

    save: function () {
        if(this.props.saveChanges){
            this.props.saveChanges(this.state.freeformData);
        }
      this.handleModeChange();
    },

    cancel: function () {
        this.handleModeChange();
    },

    handleModeChange: function () {

        if(!this.state.isMain) {
            var data = JSON.parse(JSON.stringify(this.props.freeformData));

            this.setState({ 
                freeformData :data,
                isMain: true 
            });

            this.refs.goalMain.getDOMNode().style.display="block";
            this.refs.goalEdit.getDOMNode().style.display="none";
            this.refs.buttonContainer.getDOMNode().style.display="none";
            this.state.isMain=true ;
        }
        return false;
    },

    goToEditMode: function() {

        if(this.state.isMain) {
            var data = JSON.parse(JSON.stringify(this.props.freeformData));
            this.setState({ 
                freeformData :data,
                isMain: false 
            });

            this.refs.goalMain.getDOMNode().style.display="none";
            this.refs.goalEdit.getDOMNode().style.display="block";
            this.refs.buttonContainer.getDOMNode().style.display="block";
            this.state.isMain=false;
        }
    },

    render: function() {
        return (
            React.createElement("div", {className: "article-content", onClick: this.goToEditMode}, 

                React.createElement(FreeformMain, {ref: "goalMain", description: this.props.freeformData.description}), 
                React.createElement(FreeformEdit, {ref: "goalEdit", description: this.props.freeformData.description, updateDescription: this.updateDescription}), 

                React.createElement(ButtonsContainer, {ref: "buttonContainer", save: this.save, cancel: this.cancel})
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