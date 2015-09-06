var actions = require('./actions');
var findindex = require('lodash.findindex');
var validator = require('validator');
var PubSub = require('pubsub-js');
var phoneFormatter = require('phone-formatter');
var uniq = require('lodash.uniq');

module.exports = {
  getInitialState: function() {
    return {
      changes: []
    }
  },
  shouldComponentUpdate: function (nextProps, nextState) {
    return this.props != nextProps;
  },
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
    if(this.state.changes.indexOf(name) < 0) {
      this.state.changes.push(name);
    }
    actions.changeSetting(setting);
  },
  saveHandler: function (e) {
    e.preventDefault();

    var message = "Your settings have been saved";
    var timeout = 4000;

    //Custom message and timeout on e-mail change
    if(this.state.changes.indexOf('email') > -1) {
      message = "Please check your inbox and click the confirmation link to change your e-mail."
      timeout = 6000;
    }

    PubSub.publish('settings-alert', {isOpen: true, message: message, timeout: timeout});
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
