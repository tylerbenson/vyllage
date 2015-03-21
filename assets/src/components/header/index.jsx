var React = require('react');
var PrimaryHeader = require('./primary-header');
var SecondaryHeader = require('./secondary-header');

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
      return (
        <div>
          <PrimaryHeader />
          <SecondaryHeader />
          <Menu ref="menuContainer"/>
        </div>
      );
    }
});

React.render(<HeaderContainer />, document.getElementById('header-container'));
module.exports = HeaderContainer;


