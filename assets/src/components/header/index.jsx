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
      /*
       <div className="row">
          <div className="twelve columns">
            <div className="u-pull-left header-logo"></div>
            <div className="u-pull-right header-profile">
              <img className="u-pull-left header-profile-photo" alt="" src="images/profile-photo.png"/>
              <span className="u-pull-left header-username" id="account-name">
                  <p onClick={this.handleClick}>nathan</p>
              </span>
              <button className="u-pull-left ask-advice-btn" onClick={this.askAdvise}>ask advice</button>
            </div>
          </div>
        </div>
        <Menu ref="menuContainer"/>
        */
      return (
        <div>
          <div className="row header-top">
            <div className="container">
              <div className="row">
                <div className="twelve columns">
                  <div className="u-pull-left vyllage-logo">
                    <p className="logo-title"> <span> Vyllage</span> <span className="logo-text">  Resume </span></p> 
                  </div>
                  <div className="u-pull-right">
                    <div className="header-controls">
                      <span><i className="icon ion-ios-bell"></i></span>
                      <span><i className="icon ion-gear-a"></i></span>
                      <span><img src = "images/empty-profile-icon.png" className="header-img"/></span>
                      <span className="username">Nathan B </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div className="row header-secondary">
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
        </div>
      );
    }
});

React.render(<HeaderContainer />, document.getElementById('header-container'));
module.exports = HeaderContainer;


