var React = require('react');
var Name = require('./Name');
var Address = require('./Address');
var Email = require('./Email');
var Facebook = require('./Facebook');
var FacebookAccount = require('./FacebookAccount');
var GraduationDate = require('./GraduationDate');
var Linkedin = require('./Linkedin');
var Organization = require('./Organization');
var Other = require('./Other');
var Password = require('./Password');
var PhoneNumber = require('./PhoneNumber');
var Role = require('./Role');
var SharedLinks = require('./SharedLinks');
var Twitter = require('./Twitter');

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
  other: [
  ]
};

var Settings = React.createClass({
  getInitialState: function () {
    return { settings: settings }
  },
  changeSetting : function (name, value) {
    var settings = this.state.settings;
    settings[name] = value;
    this.setState({settings: settings});
  },
  render: function () {
    var settings = this.state.settings;

    return (
      <section className='container'>
        <div className='settings'>
          <div className='row'>
            <div className='five columns settings-left'>
              <div className='topper'>
                <p>settings</p>
              </div>
              <Name value={settings.name} changeSetting={this.changeSetting} />
              <ul className='settings-profile'>
                <Role value={settings.role} changeSetting={this.changeSetting} />
                <GraduationDate value={settings.graduationDate} changeSetting={this.changeSetting} />
                <Organization value={settings.organization} changeSetting={this.changeSetting} />
                <FacebookAccount value={settings.facebookAccount} changeSetting={this.changeSetting} />
                <SharedLinks value={settings.sharedLinks} changeSetting={this.changeSetting} />
                <Password />
              </ul>
            </div>
            <div className='seven columns settings-right'>
              <div className='topper topper-right'>
                <p>account</p>
              </div>
              <ul className='settings-account'>
                <li className='row settings-account-item'>
                  <div className='offset-by-eight four columns text-center'>
                    <span className=''>visible to:</span>
                  </div>
                </li>
                <Email {...settings.email} organization={settings.organization} changeSetting={this.changeSetting} />
                <PhoneNumber {...settings.phoneNumber} organization={settings.organization} changeSetting={this.changeSetting} />
                <Address {...settings.address} organization={settings.organization} changeSetting={this.changeSetting} />
                <Twitter {...settings.twitter} organization={settings.organization} changeSetting={this.changeSetting} />
                <Linkedin {...settings.linkedin} organization={settings.organization} changeSetting={this.changeSetting} />
                <Facebook {...settings.facebook} organization={settings.organization} changeSetting={this.changeSetting} />
                <Other {...settings.other} organization={settings.organization} changeSetting={this.changeSetting} />
                <li className='row settings-account-item'>
                  <div className='offset-by-eight four columns text-center'>
                    <span className=''>delete account</span>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>   
      </section>  
    );
  }
});

module.exports = Settings;