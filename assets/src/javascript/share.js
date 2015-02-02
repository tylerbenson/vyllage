var PrivateLinkMain = React.createClass({displayName: "PrivateLinkMain",	

    render: function() {
		return (
            React.createElement("div", {className: "private-link info-blog main-info"}, 
                React.createElement("p", {className: "info-title"}, "private link"), 
                React.createElement("div", {className: "edit-btn-cont"}, 
                    React.createElement("button", {className: "edit-btn"})
                ), 
                React.createElement("p", {className: "info-link"}, 
                    React.createElement("a", {href: this.props.shareData.privateLink.link}, this.props.shareData.privateLink.link)
                ), 
                React.createElement("p", {className: "info-privacy"}, "expires after:", 
                    React.createElement("select", {className: "privacy-select"}, 
                        React.createElement("option", null, this.props.shareData.privateLink.expiresAfter)
                    )
                )
            )
		);
    }
});

var PrivateLinkEdit = React.createClass({displayName: "PrivateLinkEdit",    

    render: function() {
        return (
            React.createElement("div", {className: "private-link info-blog edit"}, 
                React.createElement("p", {className: "info-title"}, "private link"), 
                React.createElement("div", {className: "info-link"}, 
                    React.createElement("input", {type: "text", className: "info-input-edit", placeholder: this.props.shareData.privateLink.link, value: this.props.shareData.privateLink.link})
                ), 
                React.createElement("p", {className: "info-privacy-share"}, "expires after:", 
                    React.createElement("select", {className: "privacy-select"}, 
                        React.createElement("option", null, this.props.shareData.privateLink.expiresAfter)
                    )
                )
            )
        );
    }
});

var PrivateLink = React.createClass({displayName: "PrivateLink",   
    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(PrivateLinkMain, {shareData: this.props.shareData}), 
                React.createElement(PrivateLinkEdit, {shareData: this.props.shareData})
            )
        );
    }
});

var PublicLinkMain = React.createClass({displayName: "PublicLinkMain",    

    render: function() {
        return (
            React.createElement("div", {className: "public-link info-blog main-info"}, 
                React.createElement("p", {className: "info-title"}, "public link"), 
                React.createElement("div", {className: "edit-btn-cont"}, 
                    React.createElement("button", {className: "edit-btn"})
                ), 
                React.createElement("p", {className: "info-link"}, 
                    React.createElement("a", {href: this.props.shareData.publicLink.link}, this.props.shareData.publicLink.link)
                ), 
                React.createElement("p", {className: "info-privacy"}, "visible to:", 
                    React.createElement("select", {className: "privacy-select"}, 
                        React.createElement("option", null, this.props.shareData.publicLink.visibleTo)
                    )
                )
            )
        );
    }
});


var PublicLinkEdit = React.createClass({displayName: "PublicLinkEdit",    

    render: function() {
        return (
            React.createElement("div", {className: "public-link info-blog edit"}, 
                React.createElement("p", {className: "info-title"}, "public link"), 
                React.createElement("div", {className: "info-link"}, 
                    React.createElement("input", {type: "text", className: "info-input-edit", name: "email", placeholder: "https://vyllage.com/nathanbenson123", value: this.props.shareData.publicLink.link})
                ), 
                React.createElement("p", {className: "info-privacy-public"}, "visible to:", 
                    React.createElement("select", {className: "privacy-select"}, 
                        React.createElement("option", null, this.props.shareData.publicLink.visibleTo)
                    )
                )
            )
        );
    }
});

var PublicLink = React.createClass({displayName: "PublicLink",   
    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(PublicLinkMain, {shareData: this.props.shareData}), 
                React.createElement(PublicLinkEdit, {shareData: this.props.shareData})
            )
        );
    }
});


var LinkCantainer = React.createClass({displayName: "LinkCantainer",   

    render: function() {
        return (
             React.createElement("div", {className: "row info-blog-wrapper"}, 
                React.createElement("div", {className: "four columns"}, 
                    React.createElement(PrivateLink, {shareData: this.props.shareData})
                ), 
                 React.createElement("div", {className: "four columns"}, 
                    React.createElement(PublicLink, {shareData: this.props.shareData})
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