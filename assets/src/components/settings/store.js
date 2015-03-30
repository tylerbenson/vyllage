var Reflux = require('reflux');
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
    this.settings = {}; //assign({}, settings);
    this.localSettings = {}; //assign({}, settings);
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
    request
      .post('/account/settings')
      .set(this.tokenHeader, this.tokenValue)
      .send(this.localSettings)
      .end(function (err, res) {
        this.settings = assign({}, this.localSettings);
        this.update();
      }.bind(this))
  },
  onChangeSetting: function (key, value) {
    this.localSettings[key] = value;
  },
  onCancelSettings: function () {
    this.localSettings = assign({}, this.settings);
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