var actions = require('./actions');
var findindex = require('lodash.findindex');

module.exports = {
  changeHandler: function (name, e) {
    var index = findindex(this.props.settings, {name: name});
    if (index !== -1) {
      var setting = this.props.settings[index];
      setting.value = e.target.value;
      actions.changeSetting(setting);
    } else {
      actions.changeSetting({
        name: name,
        value: e.target.value,
        privacy: 'private'
      });
    }
  },
  saveHandler: function (e) {
    e.preventDefault();
    actions.updateSettings();
  },
  cancelHandler: function (name, e) {
    e.preventDefault();
    actions.cancelSettings();
    this.refs[name].getDOMNode().reset();
  }
};
