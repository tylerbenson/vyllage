var Reflux = require('reflux');
var request = require('superagent');

var settings = {
  name: 'Nathan Benson',
  role: 'student',
  graduationDate: 'Aug 1, 2015',
  organization: 'Org Name',
  facebookAccount: true,
  emailUpdates: 'weekly',
  sharedLinks: '',
  email: {
    value: ['nben888@gmail.com'],
    privacy: 'everyone'
  },
  phoneNumber: {
    value: '971.800.1565',
    privacy: 'none'
  },
  address: {
    value: '1906 NE 151st Cir <br /> Unit 15b <br /> Vancovuer, WA 98686',
    privacy: 'none'
  },
  twitter: {
    value: '@natespn',
    privacy: 'everyone'
  },
  linkedin: {
    value: 'www.linkedin.com/natebenson',
    privacy: 'everyone'
  },
  facebook: {
    value: 'www.facebook.com/natebenson',
    privacy: 'everyone'
  },
  others: [
    { value: 'www.natecast.com', privacy: 'everyone' }
  ],
  lastUpdate: 'Jan 1, 2014'
};

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  init: function () {
    this.settings = settings;
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
  onPutSettings: function () {
    request
      .put('/settings')
      .set('Content-Type', 'application/json')
      .send(this.settings)
      .end(function () {

      })
  },
  onChangeSetting: function (key, value) {
    this.settings[key] = value;
    this.update();
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