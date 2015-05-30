var Reflux = require('reflux');
var filter = require('lodash.filter');
var findindex = require('lodash.findindex');
var request = require('superagent');
var assign = require('lodash.assign');
var validator = require('validator');

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    var metaHeader = document.getElementById('meta_header');
    if (metaHeader) {
      this.tokenHeader = metaHeader.content;
    }
    var metaToken = document.getElementById('meta_token');
    if (metaToken) {
      this.tokenValue = metaToken.content;
    }
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
  onUpdateSettings: function (options) {
    options = options || {};
    request
      .put('/account/setting')
      .set(this.tokenHeader, this.tokenValue)
      .send(this.settings)
      .end(function (err, res) {
        this.settings = res.body || [];
        this.update();
        if (options.redirect) {
          window.location = '/resume';
        }
      }.bind(this))
  },
  onChangeSetting: function (setting) {
    var index = findindex(this.settings, {name: setting.name});
    setting = this.validateField(setting);

    if (index !== -1) {
      setting.value = this.settings[index].value;
      this.settings[index] = setting;
    } else {
      this.settings.push(setting);
    }
    this.update();
  },
  validateField: function (setting) {
    switch(setting.name) {
      case 'linkedIn':
        setting.errorMessage = validator.isURL(setting.value) ? null: "Invalid URL";
        break;
      case 'email':
        setting.errorMessage = validator.isEmail(setting.value) ? null: "Invalid E-mail";
        break;
      case 'phoneNumber':
        setting.errorMessage = (validator.isNumeric(setting.value) &&
                               (setting.value.length === 10)) ?
                                  null: "Invalid Phone Number";
        break;
      case 'twitter':
        setting.errorMessage = setting.value.length <= 140 ? null: "Invalid Twitter Username";
        break;
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