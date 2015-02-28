var React = require('react');
var _pick = require('lodash.pick');
var Suggestions = require('./RecipientSuggestions');
var validator = require('validator');

var RecipientEdit = React.createClass({
  getInitialState: function () {
    return {
      recipient: this.props.recipient,
      error: false,
      showSuggestions: false,
      position: {},
      firstNameError: false,
      lastNameError: false,
      emailError: false,
    }
  },
  componentWillReceiveProps: function (nextProps) {
    this.setState({
      recipient: nextProps.recipient,
      error: false
    });
  },
  validate: function () {
    var errors = {
      firstNameError: !this.state.recipient.firstName,
      lastNameError: !this.state.recipient.lastName,
      emailError: !validator.isEmail(this.state.recipient.email)
    }
    this.setState(errors);
    return !(errors.firstNameError || errors.lastNameError || errors.emailError)
  },
  selectSuggestion: function (recipient, e) {
    e.preventDefault();
    this.props.updateRecipient(recipient);
    this.setState({
      showSuggestions: false
    });
  },
  changeHandler: function (key, e) {
    e.preventDefault();
    var rect = e.target.getBoundingClientRect();
    console.log(rect);
    var recipient = this.state.recipient;
    recipient[key] = e.target.value
    this.setState({
      recipient: recipient,
      showSuggestions: true,
      position: {
        top: rect.bottom,
        left: rect.left
      }
    });
  },
  updateHandler: function (e) {
    e.preventDefault();
    var isValid = this.validate();
    var recipient = this.state.recipient;
    if (isValid) {
      this.props.updateRecipient(recipient);
      this.setState({
        error: false,
        showSuggestions: false
      });
    }
  },
  closeSuggestions: function () {
    this.setState({showSuggestions: false});
  },
  render: function () {
    var recipient = this.state.recipient;
    return (
      <div>
        <div className='rcpent-add row'>
          <div className='three columns'>
            <input  type='text' placeholder='First Name' value={recipient.firstName} onChange={this.changeHandler.bind(this, 'firstName')} onBlur={this.closeSuggestions} />
            {this.state.firstNameError? <p className='error'>* required </p>: null}
          </div>
          <div className='three columns'>
            <input  type='text' placeholder='Last Name' value={recipient.lastName} onChange={this.changeHandler.bind(this, 'lastName')} onBlur={this.closeSuggestions}/>
            {this.state.lastNameError? <p className='error'>* required </p>: null}

          </div>
          <div className='four columns'>
            <input type='email' placeholder='E-mail' value={recipient.email} onChange={this.changeHandler.bind(this, 'email')} onBlur={this.closeSuggestions} />
            {this.state.emailError? <p className='error'>Invalid Email</p>: null}
          </div>
          <div className='one columns'>
            <a className='add-button'><img src='images/add.png' onClick={this.updateHandler} /></a>
          </div>
         </div> 
         <Suggestions show={this.state.showSuggestions} position={this.state.position} selectSuggestion={this.selectSuggestion} />
      </div>
    );
  }
});

module.exports = RecipientEdit;

