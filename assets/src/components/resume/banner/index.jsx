var React = require('react');
var Textarea = require('react-textarea-autosize')
var actions = require('../actions');
var settingActions = require('../../settings/actions');

var Banner = React.createClass({
  getInitialState: function () {
    return {
      tagline: '',
      editMode: {
        tagline: false,
        address: false,
        email: false,
        twitter: false,
        linkedIn: false,
        phoneNumber: false
      }
    }
  },
  componentWillReceiveProps: function (nextProps) {
    if (nextProps.header.tagline !== this.props.header.tagline) {
      this.setState({tagline: nextProps.header.tagline});
    }
  },
  enableEdiMode: function (field, e) {
    e.preventDefault();
    if (this.props.header.owner) {
      var editMode = this.state.editMode;
      editMode[field] = true;
      this.setState({editMode: editMode});
    }
  },
  disableEdiMode: function (field, e) {
    e.preventDefault();
    var editMode = this.state.editMode;
    editMode[field] = false;
    this.setState({editMode: editMode});
    settingActions.updateSettings();
    actions.updateTagline(this.state.tagline);
  },
  handleChange: function (field, e) {
    e.preventDefault();
    if (field === 'tagline') {
      this.setState({tagline: e.target.value});
    } else {
      settingActions.changeSetting({name: field, value: e.target.value, privacy: "private"});
    }
  },
  render: function() {
    var header = this.props.header || {};
    return (
      <section className='banner'>
        <div className ="content">
          <div className="info">
            <div className="name">
              {(header.firstName || '') + " " + (header.middleName || '') + " " + (header.lastName || '')}
            </div>
            {header.owner || header.tagline}? <Textarea
              key={header.tagline || undefined}
              placeholder="What's your professional tagline?"
              className="transparent tagline"
              rows="1"
              autoComplete="off"
              defaultValue={header.tagline}
              onChange={this.handleChange.bind(this, 'tagline')}
              onClick={this.enableEdiMode.bind(this, 'tagline')}
              onBlur={this.disableEdiMode.bind(this, 'tagline')}
            ></Textarea>: null}
            <Textarea
              key={header.address || undefined}
              placeholder="Where is your current location?"
              className="transparent address"
              rows="1"
              autoComplete="off"
              defaultValue={header.address}
              onChange={this.handleChange.bind(this, 'address')}
              onClick={this.enableEdiMode.bind(this, 'address')}
              onBlur={this.disableEdiMode.bind(this, 'address')}
            ></Textarea>
          </div>
          <div className="contact">
            <div className='detail'>
              <i className="ion-email"></i>
              <input
                required
                type='text'
                placeholder="E-mail Address"
                key={header.email || undefined}
                className="inline transparent"
                autoComplete="off"
                defaultValue={header.email}
                onChange={this.handleChange.bind(this, 'email')}
                onClick={this.enableEdiMode.bind(this, 'email')}
                onBlur={this.disableEdiMode.bind(this, 'email')}
              />
            </div>
            <div className='detail'>
              <i className="ion-ios-telephone"></i>
              <input
                required
                type='text'
                placeholder="Contact Number"
                key={header.phoneNumber || undefined}
                className="inline transparent"
                autoComplete="off"
                defaultValue={header.phoneNumber}
                onChange={this.handleChange.bind(this, 'phoneNumber')}
                onClick={this.enableEdiMode.bind(this, 'phoneNumber')}
                onBlur={this.disableEdiMode.bind(this, 'phoneNumber')}
              />
            </div>
            <div className='detail'>
              <i className="ion-social-linkedin"></i>
              <input
                required
                type='text'
                placeholder="LinkedIn profile"
                key={header.linkedIn || undefined}
                className="inline transparent"
                autoComplete="off"
                defaultValue={header.linkedIn}
                onChange={this.handleChange.bind(this, 'linkedIn')}
                onClick={this.enableEdiMode.bind(this, 'linkedIn')}
                onBlur={this.disableEdiMode.bind(this, 'linkedIn')}
              />
            </div>
            <div className='detail'>
              <i className="ion-social-twitter"></i>
              <input
                required
                type='text'
                placeholder="Twitter Handle"
                key={header.twitter || undefined}
                className="inline transparent"
                autoComplete="off"
                defaultValue={header.twitter}
                onChange={this.handleChange.bind(this, 'twitter')}
                onClick={this.enableEdiMode.bind(this, 'twitter')}
                onBlur={this.disableEdiMode.bind(this, 'twitter')}
              />
            </div>
          </div>
        </div>
      </section>
    );
    }
});

module.exports = Banner;

