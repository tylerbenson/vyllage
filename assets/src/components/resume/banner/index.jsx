var React = require('react');
var ExecutionEnvironment = require('react/lib/ExecutionEnvironment');
var Textarea = require('react-textarea-autosize');
var Subheader = require('./Subheader');
var AddSection = require('../AddSection');
var Avatar = require('../../avatar');
var EditAvatar = require('../../avatar/EditAvatar');
var actions = require('../actions');
var settingActions = require('../../settings/actions');
var filter = require('lodash.filter');
var phoneFormatter = require('phone-formatter');
var validator = require('validator');
var clone = require('clone-deep');
var foreach = require('lodash.foreach');
var OnScroll = require('react-window-mixins').OnScroll;
var PubSub = require('pubsub-js');
var Alert = require('../../alert');
var uniq = require('lodash.uniq');
var ReactAutolink = require('react-autolink');

var urlParams = {};

var Banner = React.createClass({
  mixins: [OnScroll,ReactAutolink],
  getInitialState: function () {
    return {
      editMode: false,
      lastScroll: {
        scrollY: window.scrollY,
        timestamp: new Date(),
        timeout: {}
      },
      fields: clone(this.props.header)
    }
  },

  componentWillReceiveProps: function (nextProps) {
    //For delayed header response
    //TODO: should be converted into a promise on the store side
    if (nextProps !== this.props) {
      this.setState({fields: nextProps.header});
      if( nextProps.settings.length ){
        var fields = this.state.fields  ;
        nextProps.settings.forEach(function(field){
          if( fields.hasOwnProperty(field.name) ){
            fields[field.name] = field.value;
          }
        });
        this.setState({fields: fields});
      }

      if('edit-banner' in urlParams) {
        var field = urlParams['edit-banner'];

        if(field in this.refs){
          this.refs[field].getDOMNode().focus();
          delete urlParams['edit-banner'];
        }
      }
    }
  },

  componentWillMount: function(){
    var parser = document.createElement('a');
    parser.href = window.location.href;
    var args = {};

    parser.hash.substr(1).split('&').map(function(arg){
      var pair = arg.split('=');
      args[pair[0]] = pair[1];
    });

    urlParams = args;

    if('edit-banner' in urlParams) {
      this.setState({'editMode': true});
    }
  },
  onScroll: function(){
    this.handleScroll();
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
        name: 'siteUrl',
        value: fields.siteUrl,
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
        banner.siteUrl =  this.getRefValue('siteUrl');

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
    if(!validator.isURL(banner.siteUrl)) {
      errors.push('siteUrl');
    }
    if(!(validator.isNumeric(banner.phoneNumber)
      && banner.phoneNumber.length === 10)
      && banner.phoneNumber.trim().length !== 0) {
      errors.push('phoneNumber');
    }

    if(errors.length === 0) {
      this.notifyChange(banner);
      this.setState({fields: banner});
      settingActions.updateSettings(banner);
      this.toggleEditable(false);
      this.refs.phoneNumber.getDOMNode().value = banner.phoneNumber?phoneFormatter.format(banner.phoneNumber,"(NNN) NNN-NNNN"):'';
    }
  },
  notifyChange: function(banner){
    var message = "Your settings have been saved!";
    var timeout = 4000;

    if(banner.email !== this.state.fields.email) {
      message = "Please check your inbox and click the confirmation link to change your e-mail."
      timeout = 6000;
    }

    PubSub.publish('banner-alert', {isOpen: true, message: message, timeout: timeout});
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
  toggleSubheader: function(lastScrollTop){
    var scrollTop = window.scrollY;
    var scrollDirection = scrollTop - lastScrollTop;

    if(scrollDirection != 0) {
      var height = this.refs.banner.getDOMNode().offsetHeight;
      var subheader;
      if( this.refs.subheader != undefined ){
        subheader = this.refs.subheader.getDOMNode();
        var dragging = subheader.className.indexOf('dragging') > -1;
        var className = ' visible';

        subheader.className = subheader.className.replace(className, '');
        if(scrollTop > height && scrollDirection < 0 && !dragging) {
          subheader.className += className;
        }
      }
    }
  },
  handleScroll: function(){
    var DEBOUNCE_INTERVAL = 50;
    if(Math.abs(new Date() - this.state.lastScroll.timestamp) > DEBOUNCE_INTERVAL) {
      var scrollTop = window.scrollY;
      clearTimeout(this.state.lastScroll.timeout);

      this.setState({lastScroll: {
        scrollTop: scrollTop,
        timestamp: new Date()},
        timeout: setTimeout(function(){this.toggleSubheader(scrollTop);}.bind(this), DEBOUNCE_INTERVAL*4)
      });
    }
  },

  render: function() {
    var header = this.props.header || {};
    var fields = this.state.fields;
    var emailSetting = filter(this.props.settings, {name: 'email'})[0] || {};
    var phoneNumberSetting = filter(this.props.settings, {name: 'phoneNumber'})[0] || {};
    var siteUrlSetting = filter(this.props.settings, {name: 'siteUrl'})[0] || {};
    var isReadOnly = (!header.owner) || (header.owner && !this.state.editMode);
    var name = (header.firstName ? header.firstName : '') + ' '
             + (header.lastName ? header.lastName : '');


    return (
      <section className={(header.owner?'':'guest ') + 'banner'} ref="banner">
        <div className ="content">
          <div className="avatar-container">
            <Avatar src={header.avatarUrl} size="80" borderWidth="3">
              {this.state.editMode ? <EditAvatar /> : null}
            </Avatar>
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
          {(header.owner?
          <div className="contact">
            <div className='detail'>
              <i className="ion-email"></i>
              <input
                required
                type='text'
                placeholder="E-mail Address"
                readOnly={!header.owner}
                disabled={isReadOnly}
                key={fields.email || undefined}
                className="inline transparent email"
                autoComplete="off"
                ref="email"
                defaultValue={fields.email}
              />
              <p className='error'>{emailSetting.errorMessage}</p>
            </div>
            <div className='detail'>
              <i className="ion-ios-telephone"></i>
              <input
                required
                type='text'
                placeholder="Contact Number"
                disabled={isReadOnly}
                key={fields.phoneNumber || undefined}
                className="inline transparent phoneNumber"
                autoComplete="off"
                ref="phoneNumber"
                defaultValue={fields.phoneNumber?phoneFormatter.format(fields.phoneNumber,"(NNN) NNN-NNNN"):''}
              />
              <p className='error'>{phoneNumberSetting.errorMessage}</p>
            </div>
            <div className='detail'>
              <i className="ion-link"></i>
              { isReadOnly && fields.siteUrl ? this.autolink(fields.siteUrl, { target: "_blank" }) :
                <input
                  required
                  type='text'
                  placeholder="Website URL"
                  disabled={isReadOnly}
                  key={fields.siteUrl || undefined}
                  className="inline transparent"
                  autoComplete="off"
                  ref="siteUrl"
                  defaultValue={fields.siteUrl}
                />
              }

              <p className='error'>{siteUrlSetting.errorMessage}</p>
            </div>
          </div>
          :null)}
        </div>

        {(header.owner?
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
                <button className="edit" onClick={this.toggleEditable.bind(this, true)}>
                  <i className="ion-edit"></i>
                  <span>Edit Profile</span>
                </button>

                <AddSection sections={this.props.sections} />
              </div>
            )}
          </div>
        :null)}

        <Alert id='banner-alert' />

        {(header.owner?
          <Subheader ref="subheader" avatar={header.avatarUrl} name={name} onEditProfile={this.toggleEditable.bind(this, true)} sections={this.props.sections} />
        :null)}
      </section>
    );
    }
});

module.exports = Banner;

