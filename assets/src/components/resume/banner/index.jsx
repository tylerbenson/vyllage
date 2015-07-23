var React = require('react');
var ExecutionEnvironment = require('react/lib/ExecutionEnvironment');
var Textarea = require('react-textarea-autosize');
var Subheader = require('./Subheader');
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
      lastScroll: new Date(),
      fields: clone(this.props.header)
    }
  },
  shouldComponentUpdate: function(nextProps, nextState){
    return (nextState.editMode !== this.state.editMode || nextState.fields !== this.state.fields);
  },
  componentWillReceiveProps: function (nextProps) {
    //For delayed header response
    //TODO: should be converted into a promise on the store side
    if (nextProps.header !== this.props.header) {
      this.setState({fields: nextProps.header});
    }
  },
  componentDidMount: function() {
    if (ExecutionEnvironment.canUseDOM) {
      window.addEventListener('scroll', this.handleScroll);
    }
  },
  componentWillUnmount: function() {
    window.removeEventListener('scroll', this.handleScroll);
  },
  getRefValue: function(ref) {
    return this.refs[ref].getDOMNode().value;
  },
  triggerChanges: function(fields) {
    var settings = [
      {
        errorMessage: null,
        name: 'address',
        value: fields.address,
        privacy: 'private'
      },
      {
        errorMessage: null,
        name: 'email',
        value: fields.email,
        privacy: 'private'
      },
      {
        errorMessage: null,
        name: 'phoneNumber',
        value: fields.phoneNumber,
        privacy: 'private'
      },
      {
        errorMessage: null,
        name: 'twitter',
        value: fields.twitter,
        privacy: 'private'
      }
    ];

    for (var i = 0; i < settings.length; i++) {
      settingActions.changeSetting(settings[i]);
    };

    return settings;
  },
  saveChanges: function(){
    var banner = clone(this.state.fields);
        banner.tagline =  this.getRefValue('tagline'),
        banner.address =  this.getRefValue('address'),
        banner.email =  this.getRefValue('email'),
        banner.phoneNumber =  phoneFormatter.normalize(this.getRefValue('phoneNumber')),
        banner.twitter =  this.getRefValue('twitter')

    if(banner.tagline !== this.state.fields.tagline) {
      var fields = clone(this.state.fields);
      fields.tagline = banner.tagline;
      this.setState({fields: fields});
      this.props.header.tagline = banner.tagline;
      actions.updateTagline(banner.tagline);
    }

    var settings = this.triggerChanges(banner);
    var errors = [];

    //Validation
    if(!validator.isEmail(banner.email)) {
      errors.push('email');
    }
    if(!(validator.isNumeric(banner.phoneNumber)
      && banner.phoneNumber.length === 10)
      && banner.phoneNumber.trim().length !== 0) {
      errors.push('phoneNumber');
    }
    if(banner.twitter.length > 140) {
      errors.push('twitter');
    }

    if(errors.length === 0) {
      this.setState({fields: banner});
      settingActions.updateSettings();
      this.toggleEditable(false);
      this.refs.phoneNumber.getDOMNode().value = banner.phoneNumber?phoneFormatter.format(banner.phoneNumber,"(NNN) NNN-NNNN"):'';
    }
  },
  discardChanges: function(){
    foreach(this.refs, function(n, key){
      var component = this.refs[key].getDOMNode();
      //Revert to state value
      component.value = this.state.fields[key];

      var setting = filter(this.props.settings, {name: key})[0] || {};
      setting.errorMessage = null;
    }.bind(this));

    this.refs.phoneNumber.getDOMNode().value = this.state.fields.phoneNumber?phoneFormatter.format(this.state.fields.phoneNumber,"(NNN) NNN-NNNN"):'';
    this.toggleEditable(false);
  },
  scrollToTop: function(scrollFactor){
    if(document.body.scrollTop === 0) {
      return;
    }

    var scrollFactor = scrollFactor || 1;
    document.body.scrollTop -= (scrollFactor * 2);
    setTimeout(this.scrollToTop.bind(this, scrollFactor+1), 10);
  },
  toggleEditable: function(flag){
    var flag = flag || false;
    if(flag) {
      this.scrollToTop();
    }
    this.setState({editMode: flag});
  },
  handleScroll: function(){
    var DEBOUNCE_INTERVAL = 50;
    if(Math.abs(new Date() - this.state.lastScroll) > DEBOUNCE_INTERVAL) {
      var height = this.refs.banner.getDOMNode().offsetHeight;
      var subheader = this.refs.subheader.getDOMNode();
      var scrollTop = document.body.scrollTop;

      if(scrollTop > height) {
        subheader.className += ' visible';
      }
      else {
        subheader.className = subheader.className.replace(/ visible/g,'');
      }

      this.setState({lastScroll: new Date()});
    }
  },
  render: function() {
    var header = this.props.header || {};
    var fields = this.state.fields;
    var emailSetting = filter(this.props.settings, {name: 'email'})[0] || {};
    var phoneNumberSetting = filter(this.props.settings, {name: 'phoneNumber'})[0] || {};
    var twitterSetting = filter(this.props.settings, {name: 'twitter'})[0] || {};
    var isReadOnly = (!header.owner) || (header.owner && !this.state.editMode);
    var name = header.firstName + ' ' + header.lastName;

    return (
      <section className='banner' ref="banner">
        <div className ="content">
          <div className="avatar-container">
            <div className="avatar" style={{backgroundImage: 'url(' + header.avatarUrl + ')'}}></div>
          </div>
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
              <p className='error'>{twitterSetting.errorMessage}</p>
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
        {(header.owner?<Subheader ref="subheader" name={name} onEditProfile={this.toggleEditable.bind(this, true)} />:null)}
      </section>
    );
    }
});

module.exports = Banner;

