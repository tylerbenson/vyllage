var SocialMain = React.createClass({displayName: "SocialMain",	

    render: function() {
		return (
            React.createElement("div", {className: "social info-blog"}, 
                React.createElement("p", {className: "info-title"}, "social"), 
                React.createElement("div", {className: "edit-btn-cont"}, 
                    React.createElement("button", {className: "edit-btn"})
                ), 
                React.createElement("p", {className: "address"}, "twitter: ", this.props.contactData.social.twitter), 
                React.createElement("p", {className: "info-link"}, React.createElement("a", {href: "www.vyllage.com/nathanbenson", className: "soc-link"}, "www.vyllage.com/nathanbenson")
                ), 
                React.createElement("p", {className: "info-privacy"}, "visible to:", 
                    React.createElement("select", {className: "privacy-select"}, 
                        React.createElement("option", null, this.props.contactData.social.visibility)
                    )
                )
            )
		);
    }
});

var SocialEdit = React.createClass({displayName: "SocialEdit",    

    render: function() {
        return (
            React.createElement("div", {className: "social info-blog edit"}, 
                React.createElement("p", {className: "info-title-reg"}, "social"), 
                React.createElement("div", {className: "icon-wrapper-reg"}, 
                    React.createElement("img", {className: "icon add", src: "images/add.png", width: "25", height: "25"})
                ), 
                React.createElement("select", {className: "privacy-select"}, 
                    React.createElement("option", null, this.props.contactData.social.twitter)
                ), 
                React.createElement("select", {className: "privacy-select"}, 
                    React.createElement("option", null, this.props.contactData.social.twitter)
                )
            )
        );
    }
});

var Social = React.createClass({displayName: "Social",   
    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(SocialMain, {contactData: this.props.contactData}), 
                React.createElement(SocialEdit, {contactData: this.props.contactData})
            )
        );
    }
});

var ContactMain = React.createClass({displayName: "ContactMain",    

    render: function() {
        return (
            React.createElement("div", {className: "contact info-blog"}, 
                React.createElement("p", {className: "info-title"}, "contact"), 
                React.createElement("div", {className: "edit-btn-cont"}, 
                    React.createElement("button", {className: "edit-btn"})
                ), 
                React.createElement("p", {className: "address"}, "email: ", React.createElement("a", {href: this.props.contactData.contact.email}, this.props.contactData.contact.email), " "), 
                React.createElement("p", {className: "address cell"}, "cell: ", this.props.contactData.contact.home), 
                React.createElement("p", {className: "info-privacy"}, "visible to:", 
                    React.createElement("select", {className: "privacy-select"}, 
                        React.createElement("option", null, this.props.contactData.contact.visibility)
                    )
                )
            )
        );
    }
});


var ContactEdit = React.createClass({displayName: "ContactEdit",    

    render: function() {
        return (
            React.createElement("div", {className: "contact info-blog edit"}, 
                React.createElement("p", {className: "info-title"}, "contact"), 
                React.createElement("div", {className: ""}, 
                    React.createElement("input", {type: "text", className: "title-reg", name: "email", placeholder: "nathan@vyllage.com", value: this.props.contactData.contact.email}), 
                    React.createElement("input", {type: "text", className: "title-reg", name: "phone", placeholder: "phone number", value: this.props.contactData.contact.email})
                )
            )
        );
    }
});

var Contact = React.createClass({displayName: "Contact",   
    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(ContactMain, {contactData: this.props.contactData}), 
                React.createElement(ContactEdit, {contactData: this.props.contactData})
            )
        );
    }
});

var LocationMain = React.createClass({displayName: "LocationMain",    

    render: function() {
        return (
            React.createElement("div", {className: "location info-blog"}, 
                React.createElement("p", {className: "info-title"}, "location"), 
                React.createElement("div", {className: "edit-btn-cont"}, 
                    React.createElement("button", {className: "edit-btn"})
                ), 
                React.createElement("p", {className: "address"}, this.props.contactData.location.values[0]), 
                React.createElement("p", {className: "address cell"}, "Vancouver, WA 98686"), 
                React.createElement("p", {className: "info-privacy"}, "visible to:", 
                    React.createElement("select", {className: "privacy-select"}, 
                        React.createElement("option", null, this.props.contactData.location.visibility)
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
                    React.createElement(Social, {contactData: this.props.contactData})
                ), 
                 React.createElement("div", {className: "four columns"}, 
                    React.createElement(Contact, {contactData: this.props.contactData})
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
        "twitter":"@nben888999",
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
};

React.render(React.createElement(ContactCantainer, {contactData: ContactData}), document.getElementById('contact-info'));