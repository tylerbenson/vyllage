var React = require('react');
var Reflux = require('reflux');
var SettingsStore = require('./store');
var actions = require('./actions');

var SideBar = React.createClass({
  mixins: [Reflux.connect(SettingsStore)],
  select: function (type, e) {
    actions.setSettingsType(type);
  },
  render: function () {
    return (
      <div className='sidebar'>
        <a onClick={this.select.bind(this, 'profile')}
          className={this.state.activeSettingsType === 'profile' ? 'active' : ''}
        >
          <i className='ion-person'></i> Profile
        </a>
        <a onClick={this.select.bind(this, 'account')}
          className={this.state.activeSettingsType === 'account' ? 'active' : ''}
        >
          <i className='ion-card'></i> Account
        </a>
        <a onClick={this.select.bind(this, 'social')}
          className={this.state.activeSettingsType === 'social' ? 'active' : ''}
        >
          <i className='ion-social-buffer'></i> Social
        </a>
      </div>
    );
  }
});

module.exports = SideBar;