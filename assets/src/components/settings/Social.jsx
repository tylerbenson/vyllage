var React = require('react');
var Buttons = require('./Buttons');
var SettingsMixin = require('./mixin');

var Social = React.createClass({
  mixins: [SettingsMixin],
  render: function () {
    var settings = this.props.settings || {};
    return (
      <form ref='social' onSubmit={this.saveHandler}>
        <label>Url</label>
        <span>www.vyllage.com/</span>
        <input 
          ref='url'
          type='text'
          defaultValue={settings.url}
          onChange={this.changeHandler.bind(this, 'url')}
        /> 

        <label>Facebook</label>
        <input 
          ref='facebook'
          type='text'
          defaultValue={settings.facebook}
          onChange={this.changeHandler.bind(this, 'facebook')}
        /> 

        <label>Twitter</label>
        <input 
          ref='twitter'
          type='text'
          defaultValue={settings.twitter}
          onChange={this.changeHandler.bind(this, 'twitter')}
        />

        <label>Linkedin</label>
        <input 
          ref='linkedin'
          type='text'
          defaultValue={settings.linkedin}
          onChange={this.changeHandler.bind(this, 'linkedin')}
        />

        <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'social')} />
      </form>
    );
  }
});

module.exports = Social;