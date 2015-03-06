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

var Settings = React.createClass({
  render: function () {
    return (
      <section className="container">
        <div className="settings">
          <div className="row">
            <div className="five columns settings-left">
              <div className="topper">
                <p>settings</p>
              </div>
              <Name />
              <ul className="settings-profile">
                <Role />
                <GraduationDate />
                <Organization />
                <FacebookAccount />
                <SharedLinks />
                <Password />
              </ul>
            </div>
            <div className="seven columns settings-right">
              <div className="topper topper-right">
                <p>account</p>
              </div>
              <ul className="settings-account">
                <li>
                  <div className="offset-by-eight four columns text-center">
                    <span className=''>visible to:</span>
                  </div>
                </li>
                <Email />
                <PhoneNumber />
                <Address />
                <Twitter />
                <Linkedin />
                <Facebook />
                <Other />
                <li>
                  <div className="offset-by-eight four columns text-center">
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