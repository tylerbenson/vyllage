var React = require('react');
var filter = require('lodash.filter');
var Buttons = require('./Buttons');
var SettingsMixin = require('./mixin');
var Textarea = require('react-textarea-autosize');
var Datepicker = require('../datepicker');
var phoneFormatter = require('phone-formatter');

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
    var graduationDateSetting = filter(this.props.settings, {name: 'graduationDate'})[0] || {value: ''};

    if (settings.length > 0) {
      return (
        <div className='content'>
          <form ref='profile' onSubmit={this.saveHandler}>
            <label>First name</label>
            <input
              ref='firstName'
              type='text'
              defaultValue={firstNameSetting.value}
              onChange={this.changeHandler.bind(this, 'firstName')}
            />

            <label>Middle name</label>
            <input
              ref='middleName'
              type='text'
              defaultValue={middleNameSetting.value}
              onChange={this.changeHandler.bind(this, 'middleName')}
            />

            <label>Last name</label>
            <input
              ref='lastName'
              type='text'
              defaultValue={lastNameSetting.value}
              onChange={this.changeHandler.bind(this, 'lastName')}
            />

            <label>Role</label>
            <input
              disabled={true}
              ref='role'
              type='text'
              defaultValue={roleSetting.value}
              onChange={this.changeHandler.bind(this, 'role')}
            />

            <label>Organization Name</label>
            <input
              disabled={true}
              ref='organization'
              type='text'
              defaultValue={organizationSetting.value}
              onChange={this.changeHandler.bind(this, 'organization')}
            />
            <label>Graduation Date</label>
            <Datepicker
              name='graduationDate'
              date={graduationDateSetting.value}
              setDate={this.changeHandler}
            >
              <input
                type='text'
                autoComplete={false}
              />
            </Datepicker>

            <label>Address</label>
            <Textarea
              ref='address'
              defaultValue={addressSetting.value}
              onChange={this.changeHandler.bind(this, 'address')}
            ></Textarea>

            <label>Contact No.</label>
            <input
              ref='phoneNumber'
              type='text'
              defaultValue={phoneNumberSetting.value?phoneFormatter.format(phoneNumberSetting.value, "(NNN) NNN-NNNN"):''}
              onChange={this.changeHandler.bind(this, 'phoneNumber')} />
            <p className='error'>{phoneNumberSetting.errorMessage}</p>
            <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'profile')} />
          </form>
        </div>
      );
    } else {
      return null;
    }
  }
});

module.exports = Profile;