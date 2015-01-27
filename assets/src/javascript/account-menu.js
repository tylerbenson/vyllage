var Account = React.createClass({displayName: "Account",	

	handleClick: function(){
		var elem = document.getElementById('menu-container');
		if(elem.style.display =='block'){
			elem.style.display ='none'
		} else {
			elem.style.display ='block'
		}
	},

    render: function() {
		return (
			React.createElement("p", {onClick: this.handleClick}, "nathan")
		);
    }
});

var Menu = React.createClass({displayName: "Menu",	

    render: function() {
		return (
			React.createElement("ul", {class: "account-menu"}, 
				React.createElement("li", null, "profile"), 
				React.createElement("li", null, "account"), 
				React.createElement("li", null, "sign out")
			)
		);
    }
});

React.render(
    React.createElement(Account, null),
    document.getElementById('account-name')
);

React.render(
    React.createElement(Menu, null),
    document.getElementById('account-menu')
);
