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
  facebookAccount: 'linked',
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
              <Name value={settings.name} />
              <ul className='settings-profile'>
                <Role value={settings.role} />
                <GraduationDate value={settings.graduationDate} />
                <Organization value={settings.organization}/>
                <FacebookAccount value={settings.facebookAccount}/>
                <SharedLinks value={settings.sharedLinks}/>
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
                <Email {...settings.email}/>
                <PhoneNumber {...settings.phoneNumber} />
                <Address {...settings.address} />
                <Twitter {...settings.twitter} />
                <Linkedin {...settings.linkedin} />
                <Facebook {...settings.facebook} />
                <Other {...settings.other} />
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