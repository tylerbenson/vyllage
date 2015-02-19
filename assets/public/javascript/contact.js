var Social = React.createClass({displayName: "Social",

    getInitialState: function() {
        return {data: '',
                editMode: false}; 
    },

    componentWillMount : function() {
        this.setState({data: this.props.data,
                editMode: false});
    },

    edit: function() {
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

            if(this.props.updateSocialData){
                this.props.updateSocialData(this.state.data);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updateValue: function (type, data) {
        if(type=="twitter"){
            this.state.data.twitter = data;
        } else if(type=="linkedin"){
            this.state.data.linkedin = data;
        }
    },

    render: function() {
		return (
            React.createElement("div", {className: "social info-blog"}, 
                React.createElement("p", {className: "info-title"}, "Social"), 
                React.createElement("div", {className: "edit-btn-cont u-pull-left", onClick: this.edit}, 
                    React.createElement("button", {className: "edit-btn"})
                ), 
                React.createElement("div", {className: "contact-data-cont u-pull-left"}, 
                    React.createElement("p", {className: "contact-link", ref: "twitterMain"}, 
                        "twitter: ", React.createElement("a", {href: "", className: "soc-link"}, 
                             this.state.data.twitter
                        )
                    ), 
                    React.createElement(EditField, {ref: "twitterEdit", type: 'twitter', data: this.props.data.twitter, updateValue: this.updateValue}), 
                    React.createElement("p", {className: "contact-link", ref: "linkedinMain"}, 
                        "linkedin: ", React.createElement("a", {href: "", className: "soc-link"}, 
                            this.state.data.linkedin
                        )
                    ), 
                    React.createElement(EditField, {ref: "linkedinEdit", type: 'linkedin', data: this.props.data.linkedin, updateValue: this.updateValue}), 
                    React.createElement("p", {className: "info-privacy"}, "visible to:", 
                        React.createElement("select", {className: "privacy-select"}, 
                            React.createElement("option", null, this.state.data.visibility)
                        )
                    )
                )
            )
		);
    }
});

var EditField = React.createClass({displayName: "EditField",    

    getInitialState: function() {
        return {value: this.props.data}; 
    },

    componentWillMount: function () {
        this.state.value = this.props.data;
    },

    handleChange: function(event) {
        this.setState({value: event.target.value});

        if (this.props.updateValue) {
            this.props.updateValue(this.props.type, event.target.value);
        }
    },

    render: function() {
        var value = this.state.value;

        return (
            React.createElement("div", {className: "contact-link edit"}, 
                React.createElement("input", {type: "text", className: "soc-link edit", value: value, onChange: this.handleChange})
            )
        );
    }
});

var Contact = React.createClass({displayName: "Contact",

    getInitialState: function() {
        return {data: '',
                editMode: false}; 
    },

    componentWillMount : function() {
        this.setState({data: this.props.data,
                editMode: false});
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

            if(this.props.updateContactData){
                this.props.updateContactData(this.state.data);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updateValue: function (type, data) {
        if(type=="email"){
            this.state.data.email = data;
        } else if(type=="cell"){
            this.state.data.cell = data;
        }
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
                             this.state.data.email)
                    ), 
                    React.createElement(EditField, {ref: "emailEdit", type: 'email', data: this.props.data.email, updateValue: this.updateValue}), 
                    React.createElement("p", {className: "contact-link", ref: "cellMain"}, 
                        "cell: ", React.createElement("a", {href: "", className: "soc-link"}, 
                            this.state.data.cell)
                    ), 
                    React.createElement(EditField, {ref: "cellEdit", type: 'cell', data: this.props.data.cell, updateValue: this.updateValue}), 
                    React.createElement("p", {className: "info-privacy"}, "visible to:", 
                        React.createElement("select", {className: "privacy-select"}, 
                            React.createElement("option", null, this.state.data.visibility)
                        )
                    )
                )
            )
        );
    }
});

var LocationMain = React.createClass({displayName: "LocationMain",

     getInitialState: function() {
        return {data: '',
                editMode: false}; 
    },

    componentWillMount : function() {
        this.setState({data: this.props.data,
                editMode: false});
    },

    edit: function() {
        if(!this.state.editMode){
            this.refs.addressOneEdit.getDOMNode().style.display="block" ;
            this.refs.addressOneMain.getDOMNode().style.display="none" ;

            this.refs.addressTwoEdit.getDOMNode().style.display="block" ;
            this.refs.addressTwoMain.getDOMNode().style.display="none" ;
        } else {
            this.refs.addressOneEdit.getDOMNode().style.display="none";
            this.refs.addressOneMain.getDOMNode().style.display="";

            this.refs.addressTwoEdit.getDOMNode().style.display="none" ;
            this.refs.addressTwoMain.getDOMNode().style.display="block" ;

            if(this.props.updateLocationData){
                this.props.updateLocationData(this.state.data);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updateValue: function (type, data) {
        if(type=="addressOne"){
            this.state.data.values[0] = data;
        } else if(type=="addressTwo"){
            this.state.data.values[1] = data;
        }
    },

    render: function() {
        return (
            React.createElement("div", {className: "info-blog location-blog"}, 
                React.createElement("p", {className: "info-title"}, "Location"), 
                React.createElement("div", {className: "edit-btn-cont u-pull-left", onClick: this.edit}, 
                    React.createElement("button", {className: "edit-btn"})
                ), 
                React.createElement("div", {className: "contact-data-cont location-contact-data-cont u-pull-left"}, 
                    React.createElement("p", {className: "contact-link", ref: "addressOneMain"}, 
                       this.state.data.values[0]
                    ), 
                    React.createElement(EditField, {ref: "addressOneEdit", type: 'addressOne', data: this.state.data.values[0], updateValue: this.updateValue}), 
                    
                    React.createElement("p", {className: "contact-link", ref: "addressTwoMain"}, 
                       this.state.data.values[1]
                    ), 
                    React.createElement(EditField, {ref: "addressTwoEdit", type: 'addressTwo', data: this.state.data.values[1], updateValue: this.updateValue}), 
                 
                    React.createElement("p", {className: "info-privacy"}, "visible to:", 
                        React.createElement("select", {className: "privacy-select"}, 
                            React.createElement("option", null, this.state.data.visibility)
                        )
                    )
                )
            )
        );
    }
});

var ContactCantainer = React.createClass({displayName: "ContactCantainer",

   getInitialState: function() {
        return {
            isMain: true,
            contactData: ''
        };
    },

   componentWillMount: function() {
        // ajax call will go here and fetch the whoole data
        this.setState({contactData: ContactData,
                        isMain: true});
    },

    updateSocialData: function(socialData){
        this.state.contactData.social = socialData;
        this.setState({contactData: this.state.contactData});
    },

    updateContactData: function(contactData){
        this.state.contactData.contact = contactData;
        this.setState({contactData: this.state.contactData});
    },

    updateLocationData: function(locationData){
        this.state.contactData.location = locationData;
        this.setState({contactData: this.state.contactData});
    },

    render: function() {
        return (
            React.createElement("div", {className: "row info-blog-wrapper"}, 
                React.createElement("div", {className: "four columns"}, 
                    React.createElement(Social, {data: this.state.contactData.social, updateSocialData: this.updateSocialData})
                ), 
                 React.createElement("div", {className: "four columns"}, 
                    React.createElement(Contact, {data: this.state.contactData.contact, updateContactData: this.updateContactData})
                ), 
                React.createElement("div", {className: "four columns"}, 
                    React.createElement(LocationMain, {data: this.state.contactData.location, updateLocationData: this.updateLocationData})
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

React.render(React.createElement(ContactCantainer, null), document.getElementById('contact-info'));