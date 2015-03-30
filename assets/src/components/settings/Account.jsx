var React = require('react');
var filter = require('lodash.filter');
var SettingsMixin = require('./mixin');
var Buttons = require('./Buttons');

var Account = React.createClass({
  mixins: [SettingsMixin],
  render: function () {
    var settings = this.props.settings || {};
    var emailSetting = filter(this.props.settings, {name: 'email'});
    return (
      <form ref='account' onSubmit={this.saveHandler}>
        <label>E-mail</label>
        <input 
          ref='email'
          type='text'
          defaultValue={emailSettings.value}
          onChange={this.changeHandler.bind(this, 'email')}
        /> 

        <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'account')} />
      </form>
    );
  }
});

module.exports = Account;