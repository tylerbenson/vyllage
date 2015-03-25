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

  exportMenu: function() {
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
        <PrimaryHeader accountMenu={this.accountMenu} name={this.props.name}/>
        <SecondaryHeader exportMenu={this.exportMenu}/>
        <AccountMenu ref="accountMenu"/>
        <ExportMenu ref="exportmenu"/>
      </div>
    );
  }
});

var name = document.getElementById('header-container').getAttribute('name');
React.render(<HeaderContainer name={name}/>, document.getElementById('header-container'));

module.exports = HeaderContainer;


