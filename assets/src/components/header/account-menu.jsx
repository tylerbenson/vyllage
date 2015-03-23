var React = require('react');

var AccountMenu = React.createClass({  

    signOut: function() {
      window.location.pathname = "logout";
    },
    account: function () {
      window.location.pathname = "/account/";
    },
    render: function() {
      return (
        <div id="account-container" className="menu-container">
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

module.exports = AccountMenu;


