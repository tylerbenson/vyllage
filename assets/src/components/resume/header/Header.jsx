var React = require('react');
var Headline = require('./Headline');
var Tagline = require('./Tagline');
// var actions = require('../actions');

var Header = React.createClass({ 
  render: function() {
    var profileData = this.props.profileData || {};
    var contactData = this.props.contactData || {};
    console.log(profileData, contactData);
    var tagline = profileData.tagline || '';
    return (
      <section id="resume-contactInfo">
        <div className ="container">
          <div className="row">
            <div className="six columns">
              <div className="profileInfo">
                <Headline {...profileData} />
                <Tagline tagline={tagline} />
                <p className="address">some address</p>
              </div>
            </div>
            <div className="six columns">
              <div className="contactInfo">
                <p className="email"> 
                  <i className="icon ion-email"></i>
                  john@gmail.com
                </p>
                <p className="mobile">
                  <i className="icon ion-ios-telephone"></i>
                 12345678
                </p>
                <p className="twitter">
                  <i className="icon ion-social-twitter"></i>
                  @john
                </p>
                <p className="linkedin">
                  <i className="icon ion-social-linkedin"></i>
                  linkedin/john
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>
    );
    }
});

module.exports = Header;

