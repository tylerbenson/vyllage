var React = require('react');
var actions = require('./actions');

var SideBar = React.createClass({
  select: function (type, e) {
    actions.setSettingsType(type);
  },
  render: function () {
    return (
      <div className='sidebar'>
        <a onClick={this.select.bind(this, 'profile')}>
          <i className='ion-person'></i> PROFILE
        </a>
        <a onClick={this.select.bind(this, 'account')}>
          <i className='ion-key'></i> ACCOUNT
        </a>
        <a onClick={this.select.bind(this, 'social')}>
          <i className='ion-social-buffer'></i> SOCIAL
        </a>
      </div>
    );
  }
});

module.exports = SideBar;