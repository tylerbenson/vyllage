var React = require('react');
var Textarea = require('react-textarea-autosize')
var actions = require('../actions');
var settingActions = require('../../settings/actions');
var filter = require('lodash.filter');
var phoneFormatter = require('phone-formatter');
var validator = require('validator');
var clone = require('clone-deep');
var foreach = require('lodash.foreach');

var Banner = React.createClass({
  getInitialState: function () {
    return {
      editMode: false,
      fields: clone(this.props.header)
    }
  },
  componentWillReceiveProps: function (nextProps) {
    //For delayed header response
    //TODO: should be converted into a promise on the store side
    if (nextProps.header !== this.props.header) {
      this.setState({fields: nextProps.header});
    }
  },
  getRefValue: function(ref) {
    return this.refs[ref].getDOMNode().value;
  },
  saveChanges: function(){
    var errors = [];
    var banner = clone(this.state.fields);
        banner.tagline =  this.getRefValue('tagline'),
        banner.address =  this.getRefValue('address'),
        banner.email =  this.getRefValue('email'),
        banner.phoneNumber =  this.getRefValue('phoneNumber'),
        banner.twitter =  this.getRefValue('twitter')

    if(banner.tagline !== this.state.fields.tagline) {
      var fields = clone(this.state.fields);
      fields.tagline = banner.tagline;
      this.setState({fields: fields});
      this.props.header.tagline = banner.tagline;
      actions.updateTagline(banner.tagline);
    }

    //Validation
    if(!validator.isEmail(banner.email)) {
      errors.push('email');
    }
    if(!(validator.isNumeric(banner.phoneNumber)
      && banner.phoneNumber.length === 10)
      && banner.phoneNumber.trim().length !== 0) {
      errors.push('phoneNumber');
    }

    if(errors.length > 0) {
      console.log(errors);
    }
    else {
      settingActions.changeSetting({
        name: 'address',
        value: banner.address,
        privacy: 'private'
      });
      settingActions.changeSetting({
        name: 'email',
        value: banner.email,
        privacy: 'private'
      });
      settingActions.changeSetting({
        name: 'phoneNumber',
        value: banner.phoneNumber,
        privacy: 'private'
      });
      settingActions.changeSetting({
        name: 'twitter',
        value: banner.twitter,
        privacy: 'private'
      });

      settingActions.updateSettings();

      // this.setState({fields: banner});
      // this.props.header = banner;

      this.toggleEditable(false);
    }

    // switch(field) {
    //   case 'email':
    //     if(validator.isEmail(e.target.value)) {
    //       settingActions.updateSettings();
    //     }
    //     else {
    //       e.target.value = header.email;
    //     }
    //     break;
    //   case 'phoneNumber':
    //     var phoneNumberValue = phoneFormatter.normalize(e.target.value);
    //     if(validator.isNumeric(phoneNumberValue) && phoneNumberValue.length === 10) {
    //       e.target.value = phoneFormatter.format(phoneNumberValue, "(NNN) NNN-NNNN");
    //       settingActions.changeSetting({name: field, value: phoneNumberValue, privacy: "private"});
    //     }
    //     else {
    //       e.target.value = phoneFormatter.format(header.phoneNumber, "(NNN) NNN-NNNN");
    //     }
    //     break;
    //   default:
    //     settingActions.updateSettings();
    // }
  },
  discardChanges: function(){
    foreach(this.refs, function(n, key){
      var component = this.refs[key].getDOMNode();
      //Revert to state value
      component.value = this.state.fields[key];
    }.bind(this));

    this.toggleEditable(false);
  },
  toggleEditable: function(flag){
    var flag = flag || false;
    this.setState({editMode: flag});
  },
  render: function() {
    var header = this.props.header || {};
    var fields = this.state.fields;
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
            {(header.owner || fields.tagline)? <Textarea
              key={fields.tagline || undefined}
              disabled={isReadOnly}
              placeholder="What's your professional tagline?"
              className="transparent tagline"
              rows="1"
              autoComplete="off"
              ref="tagline"
              defaultValue={fields.tagline}
            ></Textarea>: null}
            {(header.owner || fields.address)? <Textarea
              key={fields.address || undefined}
              disabled={isReadOnly}
              placeholder="Where is your current location?"
              className="transparent address"
              rows="1"
              autoComplete="off"
              ref="address"
              defaultValue={fields.address}
            ></Textarea>: null}
          </div>
          <div className="contact">
            {(header.owner || fields.email)? <div className='detail'>
              <i className="ion-email"></i>
              <input
                required
                type='text'
                placeholder="E-mail Address"
                readOnly={!header.owner}
                disabled={isReadOnly}
                key={fields.email || undefined}
                className="inline transparent"
                autoComplete="off"
                ref="email"
                defaultValue={fields.email}
              />
              <p className='error'>{emailSetting.errorMessage}</p>
            </div>: null}
            {(header.owner || fields.phoneNumber)? <div className='detail'>
              <i className="ion-ios-telephone"></i>
              <input
                required
                type='text'
                placeholder="Contact Number"
                disabled={isReadOnly}
                key={fields.phoneNumber || undefined}
                className="inline transparent"
                autoComplete="off"
                ref="phoneNumber"
                defaultValue={fields.phoneNumber?phoneFormatter.format(fields.phoneNumber,"(NNN) NNN-NNNN"):''}
              />
              <p className='error'>{phoneNumberSetting.errorMessage}</p>
            </div>: null}
            {(header.owner || fields.twitter)? <div className='detail'>
              <i className="ion-social-twitter"></i>
              <span className='tip'>@</span>
              <input
                required
                type='text'
                placeholder="Twitter Username"
                disabled={isReadOnly}
                key={fields.twitter || undefined}
                className="inline transparent twitter"
                autoComplete="off"
                ref="twitter"
                defaultValue={fields.twitter}
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

