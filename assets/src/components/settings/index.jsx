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
    // actions.getSettings();
  },
  render: function () {
    var SettingsNode = null;
    if (this.state.activeSettingsType === 'profile') {
      SettingsNode = <Profile {...this.props} />;
    } else if (this.state.activeSettingsType === 'account') {
      SettingsNode = <Account {...this.props} />;
    } else {
      SettingsNode = <Social {...this.props} />;
    }
    return (
      <section className='container settings'>
        <div className='four columns'>
          <SideBar />
        </div>
        <div className='eight columns'> 
          {SettingsNode}
        </div>
      </section>  
    );
  }
});

module.exports = Settings;