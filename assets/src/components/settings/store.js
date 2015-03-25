var Reflux = require('reflux');
var request = require('superagent');
var assign = require('lodash.assign');

var settings = {
  firstName: 'James',
  middleName: 'T',
  lastName: 'Franco',
  role: 'Student',
  organization: 'Carlton University',
  address: '883 Pearl Street, Sacramento',
  phoneNumber: '971-800-1565',
  email: 'nben888@gmail.com',
  twitter: '@natespn',
  linkedin: 'www.linkedin.com/natebenson',
  facebook: 'www.facebook.com/natebenson' 
};

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    this.settings = assign({}, settings);
    this.localSettings = assign({}, settings);
    this.activeSettingsType = 'profile';
  },
  onGetSettings: function () {
    request
      .get('/settings')
      .end(function (err, res) {
        if (res.status === 200) {
          // settings is added as default for temporary use. Need to be removed in production 
          this.settings = res.body || settings;
        }
      });
  },
  onUpdateSettings: function () {
    this.settings = assign({}, this.localSettings);
    this.update();
  },
  onChangeSetting: function (key, value) {
    this.localSettings[key] = value;
  },
  onCancelSettings: function () {
    this.localSettings = assign({}, this.settings);
  },
  onAddOther: function (other) {
    this.settings.others = this.settings.others.concat(other);
    this.update();
  },
  onUpdateOther: function (other, index) {
    this.settings.others[index] = other;
    this.update();
  },
  onRemoveOther: function (index) {
    this.settings.others.splice(index, 1);
    this.update();
  },
  onSetSettingsType: function (type) {
    this.activeSettingsType = type;
    this.update();
  },
  update: function () {
    // this.onPutSettings();
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