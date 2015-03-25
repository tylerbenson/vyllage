var actions = require('./actions');

module.exports = {
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
  }
};
