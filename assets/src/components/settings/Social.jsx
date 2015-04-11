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
    var linkedInSetting = filter(this.props.settings, {name: 'linkedIn'})[0] || {value: ''};
    return (
      <div className='content'>
        <form ref='social' onSubmit={this.saveHandler}>
          <label>URL</label>
          <span>www.vyllage.com/</span>
          <input
            key={urlSetting.value || undefined}
            ref='url'
            type='text'
            className='inline url'
            defaultValue={urlSetting.value}
            onChange={this.changeHandler.bind(this, 'url')}
          />

          <label>Facebook</label>
          <input
            key={facebookSetting.value || undefined}
            ref='facebook'
            type='text'
            className='padded'
            defaultValue={facebookSetting.value}
            onChange={this.changeHandler.bind(this, 'facebook')}
          />

          <label>Twitter</label>
          <input
            key={twitterSetting.value || undefined}
            ref='twitter'
            type='text'
            className='padded'
            defaultValue={twitterSetting.value}
            onChange={this.changeHandler.bind(this, 'twitter')}
          />

          <label>LinkedIn</label>
          <input
            key={linkedInSetting.value || undefined}
            ref='linkedIn'
            type='text'
            className='padded'
            defaultValue={linkedInSetting.value}
            onChange={this.changeHandler.bind(this, 'linkedIn')}
          />

          <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'social')} />
        </form>
      </div>
    );
  }
});

module.exports = Social;