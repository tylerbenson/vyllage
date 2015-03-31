var React = require('react');
var Headline = require('./Headline');
var Tagline = require('./Tagline');
var actions = require('../actions');
var settingActions = require('../../settings/actions');

var Banner = React.createClass({ 
  getInitialState: function () {
    return {
      header: this.props.header,
      editMode: {
        tagline: false,
        address: false,
        email: false,
        twitter: false,
        linkedin: false,
        phoneNumber: false
      }
    }
  }, 
  enableEdiMode: function (field, e) {
    var editMode = this.state.editMode;
    editMode[field] = true;
    this.setState({editMode: editMode});
  },
  disableEdiMode: function (field, e) {
    var editMode = this.state.editMode;
    editMode[field] = false;
    this.setState({editMode: editMode});
    settingActions.updateSettings();
  }, 
  handleChange: function (field, e) {
    if (field === 'tagline') {
      actions.updateTagline(e.target.value);
    } else {
      settingActions.changeSetting({name: field, value: e.target.value});
    }
  },
  render: function() {
    var header = this.props.header || {}; 
    console.log(this.state.editMode);
    return (
      <section className='banner'>
        <div className ="content">
          <div className="info">
            <div className="name">
              {header.firstName + " " + header.middleName + " " + header.lastName}
            </div>
            <textarea 
              key={header.tagline || undefined}
              placeholder="What's your professional tagline?"
              className="tagline"
              rows="1"
              autoComplete="off"
              defaultValue={header.tagline}
              onChange={this.handleChange.bind(this, 'tagline')}
              onClick={this.enableEdiMode.bind(this, 'tagline')}
              onBlur={this.disableEdiMode.bind(this, 'tagline')}
            ></textarea> 
            <textarea
              key={header.address || undefined}
              className="address"
              rows="1"
              autoComplete="off"
              defaultValue={header.address}
              onChange={this.handleChange.bind(this, 'address')}
              onClick={this.enableEdiMode.bind(this, 'address')}
              onBlur={this.disableEdiMode.bind(this, 'address')}
            ></textarea>
          </div>
          <div className="contact">
            <a>
              <i className="ion-email"></i>
              <input
                key={header.email || undefined}
                className="email"
                autoComplete="off"
                defaultValue={header.email}
                onChange={this.handleChange.bind(this, 'email')}
                onClick={this.enableEdiMode.bind(this, 'email')}
                onBlur={this.disableEdiMode.bind(this, 'email')}
              />
            </a>
            <a>
              <i className="ion-ios-telephone"></i>
              <input
                key={header.phoneNumber || undefined}
                className="phoneNumber"
                autoComplete="off"
                defaultValue={header.phoneNumber}
                onChange={this.handleChange.bind(this, 'phoneNumber')}
                onClick={this.enableEdiMode.bind(this, 'phoneNumber')}
                onBlur={this.disableEdiMode.bind(this, 'phoneNumber')}
              />
            </a>
            <a>
              <i className="ion-social-linkedin"></i>
              <input
                key={header.linkedin || undefined}
                className="linkedin"
                autoComplete="off"
                defaultValue={header.linkedin}
                onChange={this.handleChange.bind(this, 'linkedin')}
                onClick={this.enableEdiMode.bind(this, 'linkedin')}
                onBlur={this.disableEdiMode.bind(this, 'linkedin')}
              />
            </a>
            <a>
              <i className="ion-social-twitter"></i>
              <input
                key={header.twitter || undefined}
                className="twitter"
                autoComplete="off"
                defaultValue={header.twitter}
                onChange={this.handleChange.bind(this, 'twitter')}
                onClick={this.enableEdiMode.bind(this, 'twitter')}
                onBlur={this.disableEdiMode.bind(this, 'twitter')}
              />
            </a>
          </div>
        </div>
      </section>
    );
    }
});

module.exports = Banner;

