var React = require('react');
var SettingsMixin = require('./mixin');

var Account = React.createClass({
  mixins: [SettingsMixin],
  render: function () {
    return (
      <form ref='account' onSubmit={this.saveHandler}>
        <label>E-mail</label>
         <input 
          ref='email'
          type='text'
          defaultValue={settings.email}
          onChange={this.changeHandler.bind(this, 'email')}
        /> 
      </form>
    );
  }
});

module.exports = Account;