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
      return (
        <div>
          <div className="header-top">
            <div className="container">
              <div className="row">
                <div className="twelve columns">
                  <div className="u-pull-left vyllage-logo">
                    <p className="logo-title"> <span> Vyllage</span> <span className="logo-text">  Resume </span></p> 
                  </div>
                  <div className="u-pull-right header-controls">
                    <span><i className="icon ion-ios-bell"></i></span>
                    <span><i className="icon ion-gear-a"></i></span>
                    <span className="header-img"><i className="icon ion-person"></i></span>
                    <span className="username" onClick={this.handleClick}>Nathan B <span className="arrow-down"></span></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div className="header-secondary">
            <div className="container">
              <div className="row">
                <div className="twelve columns">
                  <div className="u-pull-left mode">Edit Mode</div>
                  <div className="u-pull-right">
                    <button className="u-pull-left ask-advice-btn" onClick={this.askAdvise}>ask advice</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <Menu ref="menuContainer"/>
        </div>
      );
    }
});

React.render(<HeaderContainer />, document.getElementById('header-container'));
module.exports = HeaderContainer;


