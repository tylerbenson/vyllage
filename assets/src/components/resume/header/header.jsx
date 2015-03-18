var React = require('react');

var Header = React.createClass({ 

    render: function() {
      return (
        <section id="resume-contactInfo">
          <div className ="container">
            <div className="row">
              <div className="six columns">
                <div className="profileInfo">
                  <p className="headline">{this.props.profileData.firstName} {this.props.profileData.middleName} {this.props.profileData.lastName}</p>
                  <p className="tagline">{this.props.profileData.tagline}</p>
                  <p className="address">{this.props.contactData.location.values[0]} </p>
                </div>
              </div>
              <div className="six columns">
                <div className="contactInfo">
                  <p className="email">{this.props.contactData.contact.email}</p>
                  <p className="mobile">{this.props.contactData.contact.cell}</p>
                  <p className="twitter">{this.props.contactData.social.twitter}</p>
                  <p className="linkedin">{this.props.contactData.social.linkedin}</p>
                </div>
              </div>
            </div>
          </div>
        </section>
      );
    }
});
module.exports = Header;
