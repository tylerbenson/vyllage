var React = require('react');
var filter = require('lodash.filter');
var Buttons = require('./Buttons');
var SettingsMixin = require('./mixin');

var Profile = React.createClass({
  mixins: [SettingsMixin],
  render: function () {
    var settings = this.props.settings || [];
    var firstNameSetting = filter(this.props.settings, {name: 'firstName'})[0] || {value: ''};
    var middleNameSetting = filter(this.props.settings, {name: 'middleName'})[0] || {value: ''};
    var lastNameSetting = filter(this.props.settings, {name: 'lastName'})[0] || {value: ''};
    var roleSetting = filter(this.props.settings, {name: 'role'})[0] || {value: ''};
    var organizationSetting = filter(this.props.settings, {name: 'organization'})[0] || {value: ''};
    var addressSetting = filter(this.props.settings, {name: 'address'})[0] || {value: ''} ;
    var phoneNumberSetting = filter(this.props.settings, {name: 'phoneNumber'})[0] || {value: ''};
    return (
      <form ref='profile' onSubmit={this.saveHandler}>
        <div>
          <label>First name</label>
          <input 
            key={firstNameSetting.value || undefined}
            ref='firstName'
            type='text'
            value={firstNameSetting.value}
            onChange={this.changeHandler.bind(this, 'firstName')}
          />
        </div>

        <div>
          <label>Middle name</label>
          <input 
            key={middleNameSetting.value || undefined}
            ref='middleName'
            type='text'
            defaultValue={middleNameSetting.value}
            onChange={this.changeHandler.bind(this, 'middleName')}
          />
        </div>

        <div>
          <label>Last name</label>
          <input 
            key={lastNameSetting.value || undefined}
            ref='lastName'
            type='text'
            defaultValue={lastNameSetting.value}
            onChange={this.changeHandler.bind(this, 'lastName')}
          />
        </div>

        <div>
          <label>Role</label>
          <input
            key={roleSetting.value || undefined}
            ref='role'
            type='text'
            defaultValue={roleSetting.value}
            onChange={this.changeHandler.bind(this, 'role')}
          />
        </div>

        <div>
          <label>Organization Name</label>
          <input 
            key={organizationSetting.value || undefined}
            ref='organization'
            type='text' 
            defaultValue={organizationSetting.value}
            onChange={this.changeHandler.bind(this, 'organization')}
          />
        </div>

        <div>
          <label>Address</label>
          <textarea 
            key={addressSetting.value || undefined}
            ref='address'
            defaultValue={addressSetting.value}
            onChange={this.changeHandler.bind(this, 'address')}
          ></textarea>
        </div>

        <div>
          <label>Contact no</label>
          <input
            key={phoneNumberSetting.value || undefined}
            ref='phoneNumber' 
            type='text'
            defaultValue={phoneNumberSetting.value}
            onChange={this.changeHandler.bind(this, 'phoneNumber')}
          />
        </div>

        <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'profile')} />
      </form>
    );
  }
});

module.exports = Profile;