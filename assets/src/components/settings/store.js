var Reflux = require('reflux');
var filter = require('lodash.filter');
var findindex = require('lodash.findindex');
var request = require('superagent');
var assign = require('lodash.assign');
var validator = require('validator');

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    this.tokenHeader = document.getElementById('meta_header').content,
    this.tokenValue = document.getElementById('meta_token').content;
    this.settings = [];
    this.activeSettingsType = 'profile';
  },
  onGetSettings: function () {
    request
    .get('/account/setting')
    .set('Accept', 'application/json')
    .end(function (err, res) {
      this.settings = res.body;
      this.update();
    }.bind(this));
  },
  onUpdateSettings: function () {
    request
      .put('/account/setting')
      .set(this.tokenHeader, this.tokenValue)
      .send(this.settings)
      .end(function (err, res) {
        this.settings = res.body || [];
        this.update();
        window.redirect = '/resume';
      }.bind(this))
  },
  onChangeSetting: function (setting) {
    var index = findindex(this.settings, {name: setting.name});
    setting = this.validateField(setting);
    if (index !== -1) {
      this.settings[index] = setting;
    } else {
      this.settings.push(setting);
    }
  },
  validateField: function (setting) {
    if (setting.name === 'linkedIn') {
      setting.errorMessage = validator.isURL(setting.value) ? null: "Invalid URL";
    } else if (setting.name === 'email') {
      setting.errorMessage = validator.isEmail(setting.value) ? null: "Invalid Email";
    }
    return setting;
  },
  onCancelSettings: function () {
  },
  onSetSettingsType: function (type) {
    this.activeSettingsType = type;
    this.update();
  },
  update: function () {
    this.trigger({
      settings: this.settings,
      activeSettingsType: this.activeSettingsType
    });
  },
  getInitialState: function () {
    return {
      settings: this.settings,
      activeSettingsType: this.activeSettingsType
    } ;
  }
})