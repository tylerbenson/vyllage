var React = require('react');
var Reflux = require('reflux');
var SettingsStore = require('./store');
var actions = require('./actions');
var SideBar = require('./SideBar');
var Alert = require('../alert');
var Profile = require('./Profile');
var Account = require('./Account');
var Social = require('./Social');
var Permission = require('./Permission');

var Settings = React.createClass({
  mixins: [Reflux.connect(SettingsStore)],
  componentWillMount: function () {
    actions.getSettings();
  },
  render: function () {
    var SettingsNode = null;
    if (this.state.activeSettingsType === 'profile') {
      SettingsNode = <Profile {...this.props} {...this.state} />;
    } else if (this.state.activeSettingsType === 'account') {
      SettingsNode = <Account {...this.props} {...this.state} />;
    } else if (this.state.activeSettingsType == 'social') {
      SettingsNode = <Social {...this.props} {...this.state} />;
    }else{
      SettingsNode = <Permission {...this.props} {...this.state} />;
    }
    return (
      <div className='sections'>
        <Alert id='settings-alert' />
        <section className='mini section'>
          <div className='container'>
            <SideBar />
            {SettingsNode}
          </div>
        </section>
      </div>
    );
  }
});

module.exports = Settings;