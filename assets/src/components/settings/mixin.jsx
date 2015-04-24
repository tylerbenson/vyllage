var actions = require('./actions');
var findindex = require('lodash.findindex');
var validator = require('validator');
var PubSub = require('pubsub-js');

module.exports = {
  changeHandler: function (name, e) {
    var index = findindex(this.props.settings, {name: name});
    var setting;
    if (index !== -1) {
      setting = this.props.settings[index];
      setting.value = e.target.value;
    } else {
      setting = {
        name: name,
        value: e.target.value,
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
    this.refs[name].getDOMNode().reset();
    PubSub.publish('settings-alert', {isOpen: true, message: "Your setting changes have been discarded"});
  }
};
