var React = require('react');
var Reflux = require('reflux');
var SettingsStore = require('./store');
var actions = require('./actions');
var SideBar = require('./SideBar');
var Profile = require('./Profile');
var Account = require('./Account');
var Social = require('./Social');

var Settings = React.createClass({
  mixins: [Reflux.connect(SettingsStore)],
  componentDidMount: function () {
    actions.getSettings();
  },
  render: function () {
    var SettingsNode = null;
    if (this.state.activeSettingsType === 'profile') {
      SettingsNode = <Profile {...this.props} {...this.state} />;
    } else if (this.state.activeSettingsType === 'account') {
      SettingsNode = <Account {...this.props} {...this.state} />;
    } else {
      SettingsNode = <Social {...this.props} {...this.state} />;
    }
    return (
      <section className='settings'>
        <SideBar />
        <div className='content'> 
          {SettingsNode}
        </div>
      </section>  
    );
  }
});

module.exports = Settings;