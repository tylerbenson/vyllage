var Reflux = require('reflux');
var filter = require('lodash.filter');
var findindex = require('lodash.findindex');
var request = require('superagent');
var assign = require('lodash.assign');

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    this.tokenHeader = document.getElementById('meta_header').content,
    this.tokenValue = document.getElementById('meta_token').content;
    this.settings = [];
    this.localSettings = [];
    this.activeSettingsType = 'profile';
  },
  onGetSettings: function () {
    request
      .get('/account/setting')
      .end(function (err, res) {
          this.settings = res.body;
          this.update();
      }.bind(this));
  },
  onUpdateSettings: function () {
    this.localSettings.forEach(function (setting) {
      request
      .put('/account/setting/' + setting.name)
      .set(this.tokenHeader, this.tokenValue)
      .send(setting)
      .end(function (err, res) {
        var index = findindex(this.settings, {name: setting.name});
        if (index === -1) {
          this.settings.push(res.body);
        } else {
          this.settings[index] = res.body;
        }
        this.update();
      }.bind(this))
    }.bind(this))

    this.localSettings = [];
  },
  onChangeSetting: function (setting) {
    var index = findindex(this.localSettings, {name: setting.name});
    if (index !== -1) {
      this.localSettings[index] = setting;
    } else {
      this.localSettings.push(setting);
    }
  },
  onCancelSettings: function () {
    this.localSettings = [];
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