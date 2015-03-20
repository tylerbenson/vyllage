var React = require('react');
var Headline = require('./Headline');
var Tagline = require('./Tagline');
// var actions = require('../actions');

var Header = React.createClass({ 
  render: function() {
    return (
      <section id="resume-contactInfo">
        <div className ="container">
          <div className="row">
            <div className="six columns">
              <div className="profileInfo">
                <Headline {...this.props.profileData} />
                <Tagline tagline={this.props.profileData.tagline} />
                <p className="address">{this.props.contactData.location.values[0]} </p>
              </div>
            </div>
            <div className="six columns">
              <div className="contactInfo">
                <p className="email"> 
                    <i className="icon ion-email"></i>
                    {this.props.contactData.contact.email}
                  </p>
                  <p className="mobile">
                    <i className="icon ion-ios-telephone"></i>
                   {this.props.contactData.contact.cell}
                  </p>
                  <p className="twitter">
                    <i className="icon ion-social-twitter"></i>
                    {this.props.contactData.social.twitter}
                  </p>
                  <p className="linkedin">
                     <i className="icon ion-social-linkedin"></i>
                    {this.props.contactData.social.linkedin}
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

