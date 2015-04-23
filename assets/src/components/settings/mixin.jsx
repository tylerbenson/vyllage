var actions = require('./actions');
var findindex = require('lodash.findindex');
var validator = require('validator');

module.exports = {
  getInitialState: function () {
    return {
      emailError: false,
      linkedInError: false,
    }
  },
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
    actions.updateSettings();
    window.location = '/resume';
  },
  cancelHandler: function (name, e) {
    e.preventDefault();
    actions.cancelSettings();
    this.refs[name].getDOMNode().reset();
    window.location = '/resume';
  }
};
