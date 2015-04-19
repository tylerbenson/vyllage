var React = require('react');
var filter = require('lodash.filter');
var Buttons = require('./Buttons');
var SettingsMixin = require('./mixin');
var Textarea = require('react-textarea-autosize');
var Datepicker = require('../datepicker');

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
    return (
      <div className='content'>
        <form ref='profile' onSubmit={this.saveHandler}>
          <label>First name</label>
          <input
            key={firstNameSetting.value || undefined}
            ref='firstName'
            type='text'
            className='padded'
            defaultValue={firstNameSetting.value}
            onChange={this.changeHandler.bind(this, 'firstName')}
          />

          <label>Middle name</label>
          <input
            key={middleNameSetting.value || undefined}
            ref='middleName'
            type='text'
            className='padded'
            defaultValue={middleNameSetting.value}
            onChange={this.changeHandler.bind(this, 'middleName')}
          />

          <label>Last name</label>
          <input
            key={lastNameSetting.value || undefined}
            ref='lastName'
            type='text'
            className='padded'
            defaultValue={lastNameSetting.value}
            onChange={this.changeHandler.bind(this, 'lastName')}
          />

          <label>Role</label>
          <input
            disabled={true}
            key={roleSetting.value || undefined}
            ref='role'
            type='text'
            className='padded'
            defaultValue={roleSetting.value}
            onChange={this.changeHandler.bind(this, 'role')}
          />

          <label>Organization Name</label>
          <input
            disabled={true}
            key={organizationSetting.value || undefined}
            ref='organization'
            type='text'
            className='padded'
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
              className='padded'
              autoComplete={false}
            />
          </Datepicker>

          <label>Address</label>
          <Textarea
            key={addressSetting.value || undefined}
            ref='address'
            className="padded"
            defaultValue={addressSetting.value}
            onChange={this.changeHandler.bind(this, 'address')}
          ></Textarea>

          <label>Contact No.</label>
          <input
            key={phoneNumberSetting.value || undefined}
            ref='phoneNumber'
            type='text'
            className='padded'
            defaultValue={phoneNumberSetting.value}
            onChange={this.changeHandler.bind(this, 'phoneNumber')}
          />

          <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'profile')} />
        </form>
      </div>
    );
  }
});

module.exports = Profile;