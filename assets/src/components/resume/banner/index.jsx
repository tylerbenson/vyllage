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
            {(header.owner || header.tagline)? <Textarea
              key={header.tagline || undefined}
              disabled={!header.owner}
              placeholder="What's your professional tagline?"
              className="transparent tagline"
              rows="1"
              autoComplete="off"
              defaultValue={header.tagline}
              onChange={this.handleChange.bind(this, 'tagline')}
              onClick={this.enableEdiMode.bind(this, 'tagline')}
              onBlur={this.disableEdiMode.bind(this, 'tagline')}
            ></Textarea>: null}
            {(header.owner || header.address)? <Textarea
              key={header.address || undefined}
              disabled={!header.owner}
              placeholder="Where is your current location?"
              className="transparent address"
              rows="1"
              autoComplete="off"
              defaultValue={header.address}
              onChange={this.handleChange.bind(this, 'address')}
              onClick={this.enableEdiMode.bind(this, 'address')}
              onBlur={this.disableEdiMode.bind(this, 'address')}
            ></Textarea>: null}
          </div>
          <div className="contact">
            {(header.owner || header.email)? <div className='detail'>
              <i className="ion-email"></i>
              <input
                required
                type='text'
                placeholder="E-mail Address"
                disabled={!header.owner}
                key={header.email || undefined}
                className="inline transparent"
                autoComplete="off"
                defaultValue={header.email}
                onChange={this.handleChange.bind(this, 'email')}
                onClick={this.enableEdiMode.bind(this, 'email')}
                onBlur={this.disableEdiMode.bind(this, 'email')}
              />
            </div>: null}
            {(header.owner || header.phoneNumber)? <div className='detail'>
              <i className="ion-ios-telephone"></i>
              <input
                required
                type='text'
                placeholder="Contact Number"
                disabled={!header.owner}
                key={header.phoneNumber || undefined}
                className="inline transparent"
                autoComplete="off"
                defaultValue={header.phoneNumber}
                onChange={this.handleChange.bind(this, 'phoneNumber')}
                onClick={this.enableEdiMode.bind(this, 'phoneNumber')}
                onBlur={this.disableEdiMode.bind(this, 'phoneNumber')}
              />
            </div>: null}
            {(header.owner || header.twitter)? <div className='detail'>
              <i className="ion-social-twitter"></i>
              <input
                required
                type='text'
                placeholder="Twitter Handle"
                disabled={!header.owner}
                key={header.twitter || undefined}
                className="inline transparent"
                autoComplete="off"
                defaultValue={header.twitter}
                onChange={this.handleChange.bind(this, 'twitter')}
                onClick={this.enableEdiMode.bind(this, 'twitter')}
                onBlur={this.disableEdiMode.bind(this, 'twitter')}
              />
            </div>: null}
          </div>
        </div>
      </section>
    );
    }
});

module.exports = Banner;

