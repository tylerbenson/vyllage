var React = require('react');
var filter = require('lodash.filter');
var Buttons = require('./Buttons');
var SettingsMixin = require('./mixin');

var Social = React.createClass({
  mixins: [SettingsMixin],
  linkedInHandler: function (e) {
    var re = new RegExp(/^https?:\/\//);
    var url = re.test(e.target.value) ? e.target.value: 'http://' + e.target.value
    this.changeHandler('linkedIn', {
      target: {
        value: url
      }
    })
  },
  render: function () {
    var settings = this.props.settings || [];
    // var urlSetting = filter(this.props.settings, {name: 'url'})[0] || {value: ''};
    var facebookSetting = filter(this.props.settings, {name: 'facebook'})[0] || {value: ''};
    var twitterSetting = filter(this.props.settings, {name: 'twitter'})[0] || {value: ''};
    var linkedInSetting = filter(this.props.settings, {name: 'linkedIn'})[0] || {value: ''};
    if (settings.length > 0) {
      return (
        <div className='content'>
          <form ref='social' onSubmit={this.saveHandler}>

          <label>Facebook Username</label>
          <span className='tip'>facebook.com/</span>
          <input
            ref='facebook'
            type='text'
            defaultValue={facebookSetting.value}
            className='facebook'
            onChange={this.changeHandler.bind(this, 'facebook')}
          />
          <p className='error'>{facebookSetting.errorMessage}</p>

          <label>Twitter Username</label>
          <span className='tip'>@</span>
          <input
            ref='twitter'
            type='text'
            defaultValue={twitterSetting.value}
            className='twitter'
            onChange={this.changeHandler.bind(this, 'twitter')}
          />

            <label>LinkedIn URL</label>
            <input
              ref='linkedIn'
              type='text'
              defaultValue={linkedInSetting.value}
              onChange={this.linkedInHandler}
            />
            <p className='error'>{linkedInSetting.errorMessage}</p>

            <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'social')} />
          </form>
        </div>
      );
    } else {
      return null;
    }
  }
});

module.exports = Social;

// <label>Vyllage Handle</label>
// <span className="tip">www.vyllage.com/<em>handle</em></span>
// <input
//   key={urlSetting.value || undefined}
//   ref='url'
//   type='text'
//   defaultValue={urlSetting.value}
//   onChange={this.changeHandler.bind(this, 'url')}
// />