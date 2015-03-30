var React = require('react');
var SettingsMixin = require('./mixin');
var Buttons = require('./Buttons');

var Account = React.createClass({
  mixins: [SettingsMixin],
  render: function () {
    var settings = this.props.settings || {};
    return (
      <form ref='account' onSubmit={this.saveHandler}>
        <label>E-mail</label>
        <input 
          ref='email'
          type='text'
          defaultValue={settings.email}
          onChange={this.changeHandler.bind(this, 'email')}
        /> 

        <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'account')} />
      </form>
    );
  }
});

module.exports = Account;