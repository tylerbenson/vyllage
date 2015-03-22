var React = require('react');
var PrimaryHeader = require('./primary-header');
var SecondaryHeader = require('./secondary-header');
var AccountMenu =  require('./account-menu');
var ExportMenu = require('./export-menu');

var HeaderContainer = React.createClass({  

  accountMenu: function() {
    var accountMenu = this.refs.accountMenu.getDOMNode();
    if(accountMenu.style.display ==='block') {
      accountMenu.style.display ='none';
    } else {
      accountMenu.style.display ='block';
    }
  },

  export: function() {
    var exportmenu = this.refs.exportmenu.getDOMNode();
    if(exportmenu.style.display ==='block') {
      exportmenu.style.display ='none';
    } else {
      exportmenu.style.display ='block';
    }
  },
    
  render: function() {
    return (
      <div>
        <PrimaryHeader accountMenu={this.accountMenu}/>
        <SecondaryHeader export={this.export}/>
        <AccountMenu ref="accountMenu"/>
        <ExportMenu ref="exportmenu"/>
      </div>
    );
  }
});

React.render(<HeaderContainer />, document.getElementById('header-container'));
module.exports = HeaderContainer;


