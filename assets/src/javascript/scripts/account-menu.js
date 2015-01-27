var Account = React.createClass({	

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
			<p onClick={this.handleClick}>nathan</p>
		);
    }
});

var Menu = React.createClass({	

    render: function() {
		return (
			<ul class="account-menu">
				<li>profile</li>
				<li>account</li>
				<li>sign out</li>
			</ul>
		);
    }
});

React.render(
    <Account/>,
    document.getElementById('account-name')
);

React.render(
    <Menu/>,
    document.getElementById('account-menu')
);
