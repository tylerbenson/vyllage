var Social = React.createClass({displayName: "Social",

    getInitialState: function() {
        return {data: this.props.data,
                editMode: false}; 
    },

    editPrivateLink: function() {
        if(!this.state.editMode){
            this.refs.twitterEdit.getDOMNode().style.display="block" ;
            this.refs.twitterMain.getDOMNode().style.display="none" ;

            this.refs.linkedinEdit.getDOMNode().style.display="block" ;
            this.refs.linkedinMain.getDOMNode().style.display="none" ;
        } else {
            this.refs.twitterEdit.getDOMNode().style.display="none";
            this.refs.twitterMain.getDOMNode().style.display="";

            this.refs.linkedinEdit.getDOMNode().style.display="none" ;
            this.refs.linkedinMain.getDOMNode().style.display="block" ;

            if(this.props.updatePrivateLink){
                this.props.updatePrivateLink(this.state.data.link);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updateTwitter: function (link) {
        this.state.data.link=link;
    },

    updatelinkedin: function (link) {
        this.state.data.link=link;
    },

    render: function() {
		return (
            React.createElement("div", {className: "social info-blog"}, 
                React.createElement("p", {className: "info-title"}, "Social"), 
                React.createElement("div", {className: "edit-btn-cont u-pull-left", onClick: this.editPrivateLink}, 
                    React.createElement("button", {className: "edit-btn"})
                ), 
                React.createElement("div", {className: "contact-data-cont u-pull-left"}, 
                    React.createElement("p", {className: "contact-link", ref: "twitterMain"}, 
                        "twitter: ", React.createElement("a", {href: "", className: "soc-link"}, 
                             this.props.data.twitter
                        )
                    ), 
                    React.createElement(SocialEditField, {ref: "twitterEdit", data: this.props.data, updateTwitter: this.updateTwitter}), 
                    React.createElement("p", {className: "contact-link", ref: "linkedinMain"}, 
                        "linkedin: ", React.createElement("a", {href: "", className: "soc-link"}, 
                            this.props.data.linkedin
                        )
                    ), 
                    React.createElement(SocialEditField, {ref: "linkedinEdit", data: this.props.data, updatelinkedin: this.updatelinkedin}), 
                    React.createElement("p", {className: "info-privacy"}, "visible to:", 
                        React.createElement("select", {className: "privacy-select"}, 
                            React.createElement("option", null, this.props.data.visibility)
                        )
                    )
                )
            )
		);
    }
});

var SocialEditField = React.createClass({displayName: "SocialEditField",    

    getInitialState: function() {
        return {link: this.props.data.link}; 
    },

    componentDidUpdate: function () {
        this.state.link = this.props.data.link;
    },

    handleChange: function(event) {
        this.setState({link: event.target.value});

        if (this.props.updatePrivateLink) {
            this.props.updatePrivateLink(event.target.value);
        }
    },

    render: function() {
        var link = this.state.link;

        return (
            React.createElement("div", {className: "contact-link edit"}, 
                React.createElement("input", {type: "text", className: "soc-link edit", value: link, onChange: this.handleChange})
            )
        );
    }
});

var Contact = React.createClass({displayName: "Contact",

    getInitialState: function() {
        return {data: this.props.data,
                editMode: false}; 
    },

    edit: function() {
        if(!this.state.editMode){
            this.refs.emailEdit.getDOMNode().style.display="block" ;
            this.refs.emailMain.getDOMNode().style.display="none" ;

            this.refs.cellEdit.getDOMNode().style.display="block" ;
            this.refs.cellMain.getDOMNode().style.display="none" ;
        } else {
            this.refs.emailEdit.getDOMNode().style.display="none";
            this.refs.emailMain.getDOMNode().style.display="";

            this.refs.cellEdit.getDOMNode().style.display="none" ;
            this.refs.cellMain.getDOMNode().style.display="block" ;

            if(this.props.updatePrivateLink){
                this.props.updatePrivateLink(this.state.data.link);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updateEmail: function (link) {
        this.state.data.link=link;
    },

    updateCell: function (link) {
        this.state.data.link=link;
    },

    render: function() {
        return (
            React.createElement("div", {className: "social info-blog"}, 
                React.createElement("p", {className: "info-title"}, "Contact"), 
                React.createElement("div", {className: "edit-btn-cont u-pull-left", onClick: this.edit}, 
                    React.createElement("button", {className: "edit-btn"})
                ), 
                React.createElement("div", {className: "contact-data-cont u-pull-left"}, 
                    React.createElement("p", {className: "contact-link", ref: "emailMain"}, 
                        "email: ", React.createElement("a", {href: "", className: "soc-link"}, 
                             this.props.data.email)
                    ), 
                    React.createElement(SocialEditField, {ref: "emailEdit", data: this.props.data, updateEmail: this.updateEmail}), 
                    React.createElement("p", {className: "contact-link", ref: "cellMain"}, 
                        "cell: ", React.createElement("a", {href: "", className: "soc-link"}, 
                            this.props.data.cell)
                    ), 
                    React.createElement(SocialEditField, {ref: "cellEdit", data: this.props.data, updateCell: this.updateCell}), 
                    React.createElement("p", {className: "info-privacy"}, "visible to:", 
                        React.createElement("select", {className: "privacy-select"}, 
                            React.createElement("option", null, this.props.data.visibility)
                        )
                    )
                )
            )
        );
    }
});

var LocationMain = React.createClass({displayName: "LocationMain",    

    render: function() {
        return (
            React.createElement("div", {className: "info-blog location-blog"}, 
                React.createElement("p", {className: "info-title"}, "Location"), 
                React.createElement("div", {className: "edit-btn-cont u-pull-left"}, 
                    React.createElement("button", {className: "edit-btn"})
                ), 
                React.createElement("div", {className: "contact-data-cont location-contact-data-cont u-pull-left"}, 
                    React.createElement("p", {className: "contact-link"}, 
                       this.props.contactData.location.values[0]
                    ), 
                    React.createElement("p", {className: "info-privacy"}, "visible to:", 
                        React.createElement("select", {className: "privacy-select"}, 
                            React.createElement("option", null, this.props.contactData.location.visibility)
                        )
                    )
                )
            )
        );
    }
});

var LocationEdit = React.createClass({displayName: "LocationEdit",    

    render: function() {
        return (
            React.createElement("div", {className: "location info-blog edit"}, 
                React.createElement("p", {className: "info-title"}, "location"), 
                React.createElement("div", {className: ""}, 
                    React.createElement("input", {type: "text", className: "title-reg", name: "address", placeholder: "street address", value: this.props.contactData.location.values[0]}), 
                    React.createElement("input", {type: "text", className: "industry-reg", name: "zip", placeholder: "zip code", value: this.props.contactData.location.values[0]}), 
                    React.createElement("input", {type: "text", className: "start-date-reg", name: "state", placeholder: "state", value: this.props.contactData.location.values[0]}), 
                    React.createElement("div", {className: "icon-wrapper"}, 
                        React.createElement("img", {className: "icon edit-reg", src: "images/edit.png", width: "25", height: "25"})
                    )
                )
            )
        );
    }
});

var Location = React.createClass({displayName: "Location",   
    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(LocationMain, {contactData: this.props.contactData}), 
                React.createElement(LocationEdit, {contactData: this.props.contactData})
            )
        );
    }
});

var ContactCantainer = React.createClass({displayName: "ContactCantainer",   

    render: function() {
        return (
            React.createElement("div", {className: "row info-blog-wrapper"}, 
                React.createElement("div", {className: "four columns"}, 
                    React.createElement(Social, {data: this.props.contactData.social})
                ), 
                 React.createElement("div", {className: "four columns"}, 
                    React.createElement(Contact, {data: this.props.contactData.contact})
                ), 
                React.createElement("div", {className: "four columns"}, 
                    React.createElement(Location, {contactData: this.props.contactData})
                )
            )      
        );
    }
});

var ContactData = {
    "social": {
        "twitter":"@nben888",
        "facebook":"natebenson",
        "linkedin":"www.linkedin.com/natebenson",
        "visibility":"private"
    },
    "contact": {
        "email":"nathan@vyllage.com",
        "home":"555-890-2345",
        "cell":"555-123-2345",
        "visibility":"private"
    },
    "location": {
        "values":[
            "address line1",
            "address line2",
            "..."
        ],
        "visibility":"private"
    }
}

React.render(React.createElement(ContactCantainer, {contactData: ContactData}), document.getElementById('contact-info'));