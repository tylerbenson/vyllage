var Reflux = require('reflux');

var settings = {
  name: 'Nathan Benson',
  role: 'student',
  graduationDate: 'Aug 1, 2015',
  organization: 'Org Name',
  facebookAccount: true,
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
  ]
};

module.exports = Reflux.createStore({
  listenables: require('./actions'),
  onChangeSetting: function (key, value) {
    console.log(key, value);
    this.settings[key] = value;
    this.update();
  },
  update: function () {
    this.trigger({
      settings: this.settings
    });
  },
  getInitialState: function () {
    this.settings = settings;
    return { settings: this.settings } ;
  }
})