var React = require('react');
var actions = require('./actions');

var Profile = React.createClass({
 
  render: function () {
    var settings = this.props.settings || {};
    return (
      <div>
        <label>First name</label>
        <input 
          ref='firstName'
          type='text'
          defaultValue={settings.firstName}
        /> 

        <label>Middle name</label>
        <input 
          ref='middleName'
          type='text'
          defaultValue={settings.middleName}
        /> 

        <label>Last name</label>
        <input 
          ref='lastName'
          type='text'
          defaultValue={settings.lastName}
        /> 

        <label>Role</label>
        <input
          ref='role'
          type='text'
          defaultValue={settings.role}
        />

        <label>Organization Name</label>
        <input 
          ref='organization'
          type='text' 
          defaultValue={settings.organization}
        />

        <label>Address</label>
        <textarea ref='address'>{settings.address}</textarea>

        <label>Contact no</label>
        <input
          ref='phoneNumber' 
          type='text'
          defaultValue={settings.phoneNumber}
        />

        <div>
        <
      </div>
    );
  }
});

module.exports = Profile;