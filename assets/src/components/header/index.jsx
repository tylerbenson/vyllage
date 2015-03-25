var React = require('react');

var Menu = React.createClass({  

    signOut: function() {
        window.location.pathname = "logout";
    },

    account: function () {
        window.location.pathname = "/account/";
    },

    render: function() {
        return (
            <div id="menu-container">
                <div id='triangle-up'></div>
                <div id="account-menu"> 
                    <ul className="account-menu">
                        <li>profile</li>
                        <li onClick={this.account}>account</li>
                        <li onClick={this.signOut}>sign out</li>
                    </ul>
                </div>
            </div>
        );
    }
});

var HeaderContainer = React.createClass({  
   
    handleClick: function(){
        var menuContainer = this.refs.menuContainer.getDOMNode();

        if(menuContainer.style.display ==='block') {
           menuContainer.style.display ='none'
        } else {
            menuContainer.style.display ='block'
        }
    },

    askAdvise: function() {
        var pathItems = window.location.pathname.split("/"),
            documentId;

        if(pathItems.length > 2) {
            documentId = pathItems[2];
        }

        if(documentId) {
            window.location.pathname = "resume/"+ documentId + "/ask-advice";
        }

    },
    
    render: function() {
        var name = this.props.name || 'user';
        return (
            <div>
               <div className="row">
                    <div className="twelve columns">
                        <div className="u-pull-left header-logo"></div>
                        <div className="u-pull-right header-profile">
                            <img className="u-pull-left header-profile-photo" alt="" src="images/profile-photo.png"/>
                            <span className="u-pull-left header-username" id="account-name">
                                <p onClick={this.handleClick}>{name}</p>
                            </span>
                            <button className="u-pull-left ask-advice-btn" onClick={this.askAdvise}>ask advice</button>
                        </div>
                    </div>
                </div>

                <Menu ref="menuContainer"/>
            </div>
        );
    }
});

var name = document.getElementById('header-container').getAttribute('name');
React.render(<HeaderContainer name={name}/>, document.getElementById('header-container'));

module.exports = HeaderContainer;


