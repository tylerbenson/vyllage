var PrivateLinkEdit = React.createClass({displayName: "PrivateLinkEdit",

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
            React.createElement("div", {className: "share-link edit"}, 
                React.createElement("input", {type: "text", className: "info-input-edit", value: link, onChange: this.handleChange})
            )
        );
    }
});

var PrivateLink = React.createClass({displayName: "PrivateLink",

    getInitialState: function() {
        return {data: this.props.data,
                editMode: false}; 
    },

    editPrivateLink: function() {
        if(!this.state.editMode){
            this.refs.privateLinkEdit.getDOMNode().style.display="block" ;
            this.refs.privateLinkMain.getDOMNode().style.display="none" ;
        } else {
            this.refs.privateLinkEdit.getDOMNode().style.display="none";
            this.refs.privateLinkMain.getDOMNode().style.display="block";

            if(this.props.updatePrivateLink){
                this.props.updatePrivateLink(this.state.data.link);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updatePrivateLink: function (link) {
        this.state.data.link=link;
    },

    render: function() {
		return (
            React.createElement("div", {className: "private-link info-blog main-info"}, 
                React.createElement("p", {className: "info-title"}, "private link"), 
                React.createElement("div", {className: "edit-btn-cont"}, 
                    React.createElement("button", {className: "edit-btn", onClick: this.editPrivateLink})
                ), 
                React.createElement("p", {className: "share-link", ref: "privateLinkMain"}, 
                    React.createElement("a", {href: this.props.data.link}, this.props.data.link)
                ), 
                React.createElement(PrivateLinkEdit, {ref: "privateLinkEdit", data: this.props.data, updatePrivateLink: this.updatePrivateLink}), 
                React.createElement("p", {className: "info-privacy"}, "expires after:", 
                    React.createElement("select", {className: "privacy-select"}, 
                        React.createElement("option", null, this.props.data.expiresAfter)
                    )
                )
            )
		);
    }
});

var PublicLinkEdit = React.createClass({displayName: "PublicLinkEdit",

    getInitialState: function() {
        return {link: this.props.data.link}; 
    },

    componentDidUpdate: function () {
        this.state.link = this.props.data.link;
    },

    handleChange: function(event) {
        this.setState({link: event.target.value});

        if (this.props.updatePublicLink) {
            this.props.updatePublicLink(event.target.value);
        }
    },

    render: function() {
        var link = this.state.link;

        return (
            React.createElement("div", {className: "share-link edit"}, 
                React.createElement("input", {type: "text", className: "info-input-edit", value: link, onChange: this.handleChange})
            )
        );
    }
});

var PublicLink = React.createClass({displayName: "PublicLink",  

    getInitialState: function() {
        return {data: this.props.data,
                editMode: false}; 
    },  

    editPublicLink: function() {
        if(!this.state.editMode) {
            this.refs.publicLinkEdit.getDOMNode().style.display="block";
            this.refs.publicLinkMain.getDOMNode().style.display="none" ;
         } else {
            this.refs.publicLinkEdit.getDOMNode().style.display="none";
            this.refs.publicLinkMain.getDOMNode().style.display="block" ;

            if(this.props.updatePublicLink) {
                this.props.updatePublicLink(this.state.data.link);
            }
        }

        this.state.editMode = !this.state.editMode;
    },

    updatePublicLink: function (link) {
       this.state.data.link=link;
    },

    render: function() {
        return (
            React.createElement("div", {className: "public-link info-blog main-info"}, 
                React.createElement("p", {className: "info-title"}, "public link"), 
                React.createElement("div", {className: "edit-btn-cont"}, 
                    React.createElement("button", {className: "edit-btn", onClick: this.editPublicLink})
                ), 
                React.createElement("p", {className: "share-link", ref: "publicLinkMain"}, 
                    React.createElement("a", {href: this.props.data.link}, this.props.data.link)
                ), 

                React.createElement(PublicLinkEdit, {ref: "publicLinkEdit", data: this.props.data, updatePublicLink: this.updatePublicLink}), 
                React.createElement("p", {className: "info-privacy"}, "visible to:", 
                    React.createElement("select", {className: "privacy-select"}, 
                        React.createElement("option", null, this.props.data.visibleTo)
                    )
                )
            )
        );
    }
});

var LinkCantainer = React.createClass({displayName: "LinkCantainer",   

    getInitialState: function() {
        return {ShareData: []};
    },

    componentDidMount: function() {
        // ajax call will go here and fetch the profileData

        this.setState({ShareData: ShareData});
    },

    updatePrivateLink: function (link) {
        this.state.ShareData.privateLink.link=link;
        this.setState({ShareData:  this.state.ShareData});
    },

    updatePublicLink: function (link) {
        this.state.ShareData.publicLink.link=link;
        this.setState({ShareData:  this.state.ShareData});
    },

    render: function() {
        return (
             React.createElement("div", {className: "row info-blog-wrapper"}, 
                React.createElement("div", {className: "four columns"}, 
                    React.createElement(PrivateLink, {data: this.props.shareData.privateLink, updatePrivateLink: this.updatePrivateLink})
                ), 
                 React.createElement("div", {className: "four columns"}, 
                    React.createElement(PublicLink, {data: this.props.shareData.publicLink, updatePublicLink: this.updatePublicLink})
                ), 
                React.createElement("div", {className: "four columns"}, 
                    React.createElement("div", {className: "export info-blog"}, 
                        React.createElement("p", {className: "info-title"}, "export"), 
                        React.createElement("div", {className: "exp-buttons"}, 
                            React.createElement("div", {className: "exp-button"}, "word"), 
                            React.createElement("div", {className: "exp-button"}, "PDF"), 
                            React.createElement("div", {className: "exp-button"}, "print")
                        )
                    )
                )
            )      
        );
    }
});

var ShareData = {
    "privateLink": {
        "link":"www.linkedin.com/natebenson",
        "expiresAfter":"40 days"
    },
    "publicLink": {
        "link":"www.linkedin.com/natebenson",
        "visibleTo":"public"
    }
};

React.render(React.createElement(LinkCantainer, {shareData: ShareData}), document.getElementById('share-info'));