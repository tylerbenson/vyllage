var React = require('react');
var filter = require('lodash.filter');
var Buttons = require('./Buttons');
var SettingsMixin = require('./mixin');

var Profile = React.createClass({
  mixins: [SettingsMixin],
  render: function () {
    var settings = this.props.settings || [];

    return (
      <form ref='profile' onSubmit={this.saveHandler}>
        <div>
          <label>First name</label>
          <input 
            ref='firstName'
            type='text'
            defaultValue={settings.firstName}
            onChange={this.changeHandler.bind(this, 'firstName')}
          />
        </div>

        <div>
          <label>Middle name</label>
          <input 
            ref='middleName'
            type='text'
            defaultValue={settings.middleName}
            onChange={this.changeHandler.bind(this, 'middleName')}
          />
        </div>

        <div>
          <label>Last name</label>
          <input 
            ref='lastName'
            type='text'
            defaultValue={settings.lastName}
            onChange={this.changeHandler.bind(this, 'lastName')}
          />
        </div>

        <div>
          <label>Role</label>
          <input
            ref='role'
            type='text'
            defaultValue={settings.role}
            onChange={this.changeHandler.bind(this, 'role')}
          />
        </div>

        <div>
          <label>Organization Name</label>
          <input 
            ref='organization'
            type='text' 
            defaultValue={settings.organization}
            onChange={this.changeHandler.bind(this, 'organization')}
          />
        </div>

        <div>
          <label>Address</label>
          <textarea 
            ref='address'
            onChange={this.changeHandler.bind(this, 'address')}
          >{settings.address}</textarea>
        </div>

        <div>
          <label>Contact no</label>
          <input
            ref='phoneNumber' 
            type='text'
            defaultValue={settings.phoneNumber}
            onChange={this.changeHandler.bind(this, 'phoneNumber')}
          />
        </div>

        <Buttons save={this.saveHandler} cancel={this.cancelHandler.bind(this, 'profile')} />
      </form>
    );
  }
});

module.exports = Profile;