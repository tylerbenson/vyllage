var React = require('react');
var Name = require('./Name');
var Address = require('./Address');
var Email = require('./Email');
var Facebook = require('./Facebook');
var FacebookAccount = require('./FacebookAccount');
var EmailUpdates = require('./EmailUpdates');
var GraduationDate = require('./GraduationDate');
var Linkedin = require('./Linkedin');
var Organization = require('./Organization');
var Other = require('./Other');
var OtherList = require('./OtherList');
var Password = require('./Password');
var PhoneNumber = require('./PhoneNumber');
var Role = require('./Role');
var SharedLinks = require('./SharedLinks');
var Twitter = require('./Twitter');
var Reflux = require('reflux');
var settingsStore = require('./store');

var Settings = React.createClass({
  mixins: [Reflux.connect(settingsStore)],
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
                <Organization value={settings.organization} />
                <FacebookAccount value={settings.facebookAccount} />
                <EmailUpdates value={settings.emailUpdates} />
                <SharedLinks value={settings.sharedLinks} />
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
                <Email {...settings.email} organization={settings.organization} />
                <PhoneNumber {...settings.phoneNumber} organization={settings.organization} />
                <Address {...settings.address} organization={settings.organization} />
                <Twitter {...settings.twitter} organization={settings.organization} />
                <Linkedin {...settings.linkedin} organization={settings.organization} />
                <Facebook {...settings.facebook} organization={settings.organization} />
                <OtherList others={settings.others} organization={settings.organization} />
                <Other {...settings.others} organization={settings.organization} />
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