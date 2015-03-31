var React = require('react');
var filter = require('lodash.filter');
var Buttons = require('./Buttons');
var SettingsMixin = require('./mixin');

var Social = React.createClass({
  mixins: [SettingsMixin],
  render: function () {
    var settings = this.props.settings || {};
    var urlSetting = filter(this.props.settings, {name: 'url'})[0] || {value: ''};
    var facebookSetting = filter(this.props.settings, {name: 'facebook'})[0] || {value: ''};
    var twitterSetting = filter(this.props.settings, {name: 'twitter'})[0] || {value: ''};
    var linkedinSetting = filter(this.props.settings, {name: 'linkedin'})[0] || {value: ''};
    return (
      <form ref='social' onSubmit={this.saveHandler}>
        <label>Url</label>
        <span>www.vyllage.com/</span>
        <input 
          key={urlSetting.value || undefined}
          ref='url'
          type='text'
          defaultValue={urlSetting.value}
          onChange={this.changeHandler.bind(this, 'url')}
        /> 

        <label>Facebook</label>
        <input 
          key={facebookSetting.value || undefined}
          ref='facebook'
          type='text'
          defaultValue={facebookSetting.value}
          onChange={this.changeHandler.bind(this, 'facebook')}
        /> 

        <label>Twitter</label>
        <input 
          key={twitterSetting.value || undefined}
          ref='twitter'
          type='text'
          defaultValue={twitterSetting.value}
          onChange={this.changeHandler.bind(this, 'twitter')}
        />

        <label>Linkedin</label>
        <input 
          key={linkedinSetting.value || undefined}
          ref='linkedin'
          type='text'
          defaultValue={linkedinSetting.value}
          onChange={this.changeHandler.bind(this, 'linkedin')}
        />

        <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'social')} />
      </form>
    );
  }
});

module.exports = Social;