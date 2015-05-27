var actions = require('./actions');
var findindex = require('lodash.findindex');
var validator = require('validator');
var PubSub = require('pubsub-js');
var phoneFormatter = require('phone-formatter');

module.exports = {
  changeHandler: function (name, e) {
    var index = findindex(this.props.settings, {name: name});
    var setting;
    if (index !== -1) {
      setting = this.props.settings[index];
      setting.value = name ==='phoneNumber' ? phoneFormatter.normalize(e.target.value) : e.target.value;
    } else {
      setting = {
        name: name,
        value: name ==='phoneNumber' ? phoneFormatter.normalize(e.target.value) : e.target.value,
        privacy: 'private'
      };
    }
    actions.changeSetting(setting);
  },
  saveHandler: function (e) {
    e.preventDefault();
    PubSub.publish('settings-alert', {isOpen: true, message: "Your settings have been saved"});
    actions.updateSettings();
  },
  cancelHandler: function (name, e) {
    e.preventDefault();
    actions.cancelSettings();
    // Reseting form is causing #404. So using redirect instead
    // this.refs[name].getDOMNode().reset();
    window.location = '/account/setting';
  }
};
