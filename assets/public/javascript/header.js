var Menu = React.createClass({displayName: "Menu",  

    render: function() {
        return (
            React.createElement("div", {id: "menu-container"}, 
                React.createElement("div", {id: "triangle-up"}), 
                React.createElement("div", {id: "account-menu"}, 
                    React.createElement("ul", {className: "account-menu"}, 
                        React.createElement("li", null, "profile"), 
                        React.createElement("li", null, "account"), 
                        React.createElement("li", null, "sign out")
                    )
                )
            )
        );
    }
});

var HeaderContainer = React.createClass({displayName: "HeaderContainer",  

    handleClick: function(){
        var menuContainer = this.refs.menuContainer.getDOMNode();

        if(menuContainer.style.display =='block') {
           menuContainer.style.display ='none'
        } else {
            menuContainer.style.display ='block'
        }
    },
    
    render: function() {
        return (
            React.createElement("div", null, 
               React.createElement("div", {className: "row"}, 
                    React.createElement("div", {className: "twelve columns"}, 
                        React.createElement("div", {className: "u-pull-left header-logo"}), 
                        React.createElement("div", {className: "u-pull-right header-profile"}, 
                            React.createElement("img", {className: "u-pull-left header-profile-photo", alt: "", src: "images/profile-photo.png"}), 
                            React.createElement("span", {className: "u-pull-left header-username", id: "account-name"}, 
                                React.createElement("p", {onClick: this.handleClick}, "nathan")
                            ), 
                            React.createElement("button", {className: "u-pull-left ask-advice-btn"}, "ask advice")
                        )
                    )
                ), 

                React.createElement(Menu, {ref: "menuContainer"})
            )
        );
    }
});


React.render(React.createElement(HeaderContainer, null), document.getElementById('header-container'));




