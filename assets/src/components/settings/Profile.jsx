var React = require('react');
var actions = require('./actions');
var Buttons = require('./Buttons');

var Profile = React.createClass({
  changeHandler: function (key, e) {
    actions.changeSetting({
      [key]: e.target.value
    });
  },
  saveHandler: function (e) {
    e.preventDefault();
    actions.updateSettings();
  },
  cancelHandler: function (e) {
    e.preventDefault();
    actions.cancelSettings();
    this.refs.profile.getDOMNode().reset();
  },
  render: function () {
    var settings = this.props.settings || {};
    return (
      <form ref='profile' onSubmit={this.saveHandler}>
        <label>First name</label>
        <input 
          ref='firstName'
          type='text'
          defaultValue={settings.firstName}
          onChange={this.changeHandler.bind(this, 'firstName')}
        /> 

        <label>Middle name</label>
        <input 
          ref='middleName'
          type='text'
          defaultValue={settings.middleName}
          onChange={this.changeHandler.bind(this, 'middleName')}
        /> 

        <label>Last name</label>
        <input 
          ref='lastName'
          type='text'
          defaultValue={settings.lastName}
          onChange={this.changeHandler.bind(this, 'lastName')}
        /> 

        <label>Role</label>
        <input
          ref='role'
          type='text'
          defaultValue={settings.role}
          onChange={this.changeHandler.bind(this, 'role')}
        />

        <label>Organization Name</label>
        <input 
          ref='organization'
          type='text' 
          defaultValue={settings.organization}
          onChange={this.changeHandler.bind(this, 'organization')}
        />

        <label>Address</label>
        <textarea 
          ref='address'
          onChange={this.changeHandler.bind(this, 'address')}
        >{settings.address}</textarea>

        <label>Contact no</label>
        <input
          ref='phoneNumber' 
          type='text'
          defaultValue={settings.phoneNumber}
          onChange={this.changeHandler.bind(this, 'phoneNumber')}
        />

        <Buttons save={this.saveHandler} cancel={this.cancelHandler} />
      </form>
    );
  }
});

module.exports = Profile;