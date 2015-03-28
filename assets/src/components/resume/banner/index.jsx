var React = require('react');
var Headline = require('./Headline');
var Tagline = require('./Tagline');
// var actions = require('../actions');

var Header = React.createClass({ 
  render: function() {
    var profileData = this.props.profileData || {};
    var contactData = this.props.contactData || {};
    var tagline = profileData.tagline || '';
    return (
      <section className='banner'>
        <div className ="content">
          <div className="info">
            <div className="name">
              James T. Franco
            </div>
            <textarea disabled placeholder="What's your professional tagline?" className="tagline" rows="1" autocomplete="off">Full-stack Developer &amp; UI/UX Designer</textarea>
            <div className="address">
              816 Corinthian Executive Regency,
              Ortigas Ave., Ortigas Center, Pasig City, PH
            </div>
          </div>
          <div className="contact">
            <a href="">
              <i className="ion-email"></i>
              jamesfranco@gmail.com
            </a>
            <a href="">
              <i className="ion-ios-telephone"></i>
              64 917 3984 598
            </a>
            <a href="">
              <i className="ion-social-facebook"></i>
              james.franco
            </a>
            <a href="">
              <i className="ion-social-twitter"></i>
              @thisisjames
            </a>
          </div>
        </div>
      </section>
    );
    }
});

module.exports = Header;

