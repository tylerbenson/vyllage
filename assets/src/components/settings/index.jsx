var React = require('react');
var Reflux = require('reflux');
var SettingsStore = require('./store');
var actions = require('./actions');

var Settings = React.createClass({
  mixins: [Reflux.connect(SettingsStore)],
  componentDidMount: function () {
    actions.getSettings();
  },
  render: function () {
    var settings = this.state.settings;
    return (
      <section className='container'>
        {'settings'}
      </section>  
    );
  }
});

module.exports = Settings;