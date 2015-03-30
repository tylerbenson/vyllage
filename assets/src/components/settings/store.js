var Reflux = require('reflux');
var filter = require('lodash.filter');
var findindex = require('lodash.findindex');
var request = require('superagent');
var assign = require('lodash.assign');

// var settings = {
//   firstName: 'James',
//   middleName: 'T',
//   lastName: 'Franco',
//   role: 'Student',
//   organization: 'Carlton University',
//   address: '883 Pearl Street, Sacramento',
//   phoneNumber: '971-800-1565',
//   email: 'nben888@gmail.com',
//   url:'jamesfranco',
//   twitter: '@natespn',
//   linkedin: 'www.linkedin.com/natebenson',
//   facebook: 'www.facebook.com/natebenson' 
// };

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
      .get('/account/settings')
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

      }.bind(this))
    }.bind(this))
    this.localSettings = [];
  },
  onChangeSetting: function (setting) {
    var index = findindex(this.localSettings, {accountSettingId: setting.accountSettingId});
    if (index) {
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