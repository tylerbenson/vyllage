var React = require('react');
var filter = require('lodash.filter');
var SettingsMixin = require('./mixin');
var Buttons = require('./Buttons');
var DeleteAccount = require('./DeleteAccount');

var Account = React.createClass({
  mixins: [SettingsMixin],
  render: function () {
    var settings = this.props.settings || [];
    var emailSetting = filter(this.props.settings, {name: 'email'})[0] || {value: ''};
    if (settings.length > 0) {
      return (
        <div className='content'>
          <form ref='account' onSubmit={this.saveHandler}>
            <label>E-mail</label>
            <input
              ref='email'
              type='text'
              defaultValue={emailSetting.value}
              onChange={this.changeHandler.bind(this, 'email')}
            />
            <p className='error'>{emailSetting.errorMessage}</p>

            <div className="hr"></div>

            <label>Other</label>
            <div>
              <a className="flat secondary small button normal-caps" href='account/reset-password'>
                <i className="ion-locked"></i>
                Reset Password
              </a>
            </div>

            <div>
              <a className="flat secondary small button normal-caps" href='logout'>
                <i className="ion-android-exit"></i>
                Sign out of Vyllage
              </a>
            </div>

            <DeleteAccount />

            <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'account')} />
          </form>
        </div>
      );
    } else {
      return null;
    }
  }
});

module.exports = Account;