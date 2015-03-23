var React = require('react');

var PrimaryHeader = React.createClass({  

  accountMenu: function() {
    if(this.props.accountMenu) {
      this.props.accountMenu();
    }
  },

  render: function() {
    return (
      <div className="header-top">
        <div className="container">
          <div className="row">
            <div className="twelve columns">
              <div className="u-pull-left vyllage-logo">
                <p className="logo-title"> 
                  <span> Vyllage</span> <span className="logo-text">  Resume </span>
                </p> 
              </div>
              <div className="u-pull-right header-controls">
                <span><i className="icon ion-ios-bell"></i></span>
                <span><i className="icon ion-gear-a"></i></span>
                <span className="header-img">
                  <i className="icon ion-person"></i>
                </span>
                <span className="username" onClick={this.accountMenu}>
                  Nathan B <span className="arrow-down"></span>
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
});

module.exports = PrimaryHeader;


