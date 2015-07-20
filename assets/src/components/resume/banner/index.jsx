var React = require('react');
var Textarea = require('react-textarea-autosize')
var actions = require('../actions');
var settingActions = require('../../settings/actions');
var filter = require('lodash.filter');
var phoneFormatter = require('phone-formatter');
var validator = require('validator');

var Banner = React.createClass({
  getInitialState: function () {
    return {
      tagline: '',
      editMode: false,
      editable: {
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
  enableEditMode: function (field, e) {
    e.preventDefault();
    if (this.props.header.owner) {
      var editable = this.state.editable;
      editable[field] = true;
      this.setState({editable: editable});
    }
  },
  disableEditMode: function (field, e) {
    e.preventDefault();
    var header = this.props.header || {};
    var editable = this.state.editable;
    editable[field] = false;
    this.setState({editable: editable});

    switch(field) {
      case 'tagline':
        actions.updateTagline(this.state.tagline);
        break;
      case 'email':
        if(validator.isEmail(e.target.value)) {
          settingActions.updateSettings();
        }
        else {
          e.target.value = header.email;
        }
        break;
      case 'phoneNumber':
        var phoneNumberValue = phoneFormatter.normalize(e.target.value);
        if(validator.isNumeric(phoneNumberValue) && phoneNumberValue.length === 10) {
          e.target.value = phoneFormatter.format(phoneNumberValue, "(NNN) NNN-NNNN");
          settingActions.changeSetting({name: field, value: phoneNumberValue, privacy: "private"});
        }
        else {
          e.target.value = phoneFormatter.format(header.phoneNumber, "(NNN) NNN-NNNN");
        }
        break;
      default:
        settingActions.updateSettings();
    }
  },
  handleChange: function (field, e) {
    e.preventDefault();
    if (field === 'tagline') {
      this.setState({tagline: e.target.value});
    } else {
      var value = field === 'phoneNumber' ? phoneFormatter.normalize(e.target.value) : e.target.value;
      settingActions.changeSetting({name: field, value: value, privacy: "private"});
    }
  },
  saveChanges: function(){
    this.toggleEditable(false);
  },
  discardChanges: function(){
    this.toggleEditable(false);
  },
  toggleEditable: function(flag){
    var flag = flag || false;
    this.setState({editMode: flag});
  },
  render: function() {
    var header = this.props.header || {};
    var emailSetting = filter(this.props.settings, {name: 'email'})[0] || {};
    var phoneNumberSetting = filter(this.props.settings, {name: 'phoneNumber'})[0] || {};
    var isReadOnly = (!header.owner) || (header.owner && !this.state.editMode);

    return (
      <section className='banner'>
        <div className ="content">
          <div className="info">
            <div className="name">
              {(header.firstName || '') + " " + (header.middleName || '') + " " + (header.lastName || '')}
            </div>
            {(header.owner || header.tagline)? <Textarea
              key={header.tagline || undefined}
              disabled={isReadOnly}
              placeholder="What's your professional tagline?"
              className="transparent tagline"
              rows="1"
              autoComplete="off"
              defaultValue={header.tagline}
              onChange={this.handleChange.bind(this, 'tagline')}
              onClick={this.enableEditMode.bind(this, 'tagline')}
              onBlur={this.disableEditMode.bind(this, 'tagline')}
            ></Textarea>: null}
            {(header.owner || header.address)? <Textarea
              key={header.address || undefined}
              disabled={isReadOnly}
              placeholder="Where is your current location?"
              className="transparent address"
              rows="1"
              autoComplete="off"
              defaultValue={header.address}
              onChange={this.handleChange.bind(this, 'address')}
              onClick={this.enableEditMode.bind(this, 'address')}
              onBlur={this.disableEditMode.bind(this, 'address')}
            ></Textarea>: null}
          </div>
          <div className="contact">
            {(header.owner || header.email)? <div className='detail'>
              <i className="ion-email"></i>
              <input
                required
                type='text'
                placeholder="E-mail Address"
                readOnly={!header.owner}
                disabled={isReadOnly}
                key={header.email || undefined}
                className="inline transparent"
                autoComplete="off"
                defaultValue={header.email}
                onChange={this.handleChange.bind(this, 'email')}
                onClick={this.enableEditMode.bind(this, 'email')}
                onBlur={this.disableEditMode.bind(this, 'email')}
              />
              <p className='error'>{emailSetting.errorMessage}</p>
            </div>: null}
            {(header.owner || header.phoneNumber)? <div className='detail'>
              <i className="ion-ios-telephone"></i>
              <input
                required
                type='text'
                placeholder="Contact Number"
                disabled={isReadOnly}
                key={header.phoneNumber || undefined}
                className="inline transparent"
                autoComplete="off"
                defaultValue={header.phoneNumber?phoneFormatter.format(header.phoneNumber,"(NNN) NNN-NNNN"):''}
                onChange={this.handleChange.bind(this, 'phoneNumber')}
                onClick={this.enableEditMode.bind(this, 'phoneNumber')}
                onBlur={this.disableEditMode.bind(this, 'phoneNumber')}
              />
              <p className='error'>{phoneNumberSetting.errorMessage}</p>
            </div>: null}
            {(header.owner || header.twitter)? <div className='detail'>
              <i className="ion-social-twitter"></i>
              <span className='tip'>@</span>
              <input
                required
                type='text'
                placeholder="Twitter Username"
                disabled={isReadOnly}
                key={header.twitter || undefined}
                className="inline transparent twitter"
                autoComplete="off"
                defaultValue={header.twitter}
                onChange={this.handleChange.bind(this, 'twitter')}
                onClick={this.enableEditMode.bind(this, 'twitter')}
                onBlur={this.disableEditMode.bind(this, 'twitter')}
              />
            </div>: null}
          </div>
        </div>

        <div className="actions">

          {( this.state.editMode ?
            <div className="content">
              <button onClick={this.saveChanges}>
                <i className="ion-checkmark"></i>
                <span>Save Changes</span>
              </button>
              <button onClick={this.discardChanges} className="flat">
                <span>Cancel</span>
              </button>
            </div>
            :
            <div className="content">
              <button onClick={this.toggleEditable.bind(this, true)}>
                <i className="ion-edit"></i>
                <span>Edit Profile</span>
              </button>
            </div>
          )}

        </div>
      </section>
    );
    }
});

module.exports = Banner;

