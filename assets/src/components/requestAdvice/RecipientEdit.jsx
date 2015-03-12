var React = require('react');
var _pick = require('lodash.pick');
var Suggestions = require('./RecipientSuggestions');
var validator = require('validator');
var Actions = require('./actions');

var RecipientEdit = React.createClass({
  getInitialState: function () {
    return {
      firstNameError: false,
      lastNameError: false,
      emailError: false,
    }
  },
  validate: function () {
    var errors = {
      firstNameError: !this.props.recipient.firstName,
      lastNameError: !this.props.recipient.lastName,
      emailError: !validator.isEmail(this.props.recipient.email)
    }
    this.setState(errors);
    return !(errors.firstNameError || errors.lastNameError || errors.emailError)
  },
  changeHandler: function (key, e) {
    e.preventDefault();
    Actions.changeRecipient(key, e.target.value)
    Actions.openSuggestions();
  },
  keyPress: function (e) {
    if (e.key === 'Enter') {
      if (this.props.selectedSuggestion === null) {
        this.updateHandler(e);
      } else {
        Actions.selectSuggestion(this.props.selectedSuggestion);
        this.setState(this.getInitialState());
      }
    } 

    if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
      this.changeSelectedSuggestion(e);
    } else {
      Actions.suggestionIndex(null);
    }
  },
  updateHandler: function (e) {
    e.preventDefault();
    var isValid = this.validate();
    var recipient = this.props.recipient;
    if (isValid) {
      if (this.props.selectedRecipient === null) {
        Actions.addRecipient(recipient);
      } else {
        Actions.updateRecipient(recipient, this.props.selectedRecipient)
      }
      this.setState(this.getInitialState());
    }
  },
  changeSelectedSuggestion: function (e) {
    var totalSuggestions = this.props.suggestions.recent.length + this.props.suggestions.recommended.length
    var selectedSuggestion;
    if (e.key === 'ArrowDown') {
      if (this.props.selectedSuggestion === null) {
        selectedSuggestion = 0;
      } else if (this.props.selectedSuggestion < totalSuggestions -1) {
        selectedSuggestion = this.props.selectedSuggestion + 1;
      } else {
        selectedSuggestion = this.props.selectedSuggestion;
      }
    }

    if (e.key === 'ArrowUp') {
      selectedSuggestion = (this.props.selectedSuggestion > 0)? this.props.selectedSuggestion - 1: 0;
    } 
    Actions.suggestionIndex(selectedSuggestion);
  },
  blurHandler: function () {
    Actions.closeSuggestions();
  },
  focusHandler: function () {
    var recipient = this.props.recipient;
    if (recipient.firstName || recipient.lastName || recipient.email) {
      Actions.openSuggestions();
    }
  },
  render: function () {
    var recipient = this.props.recipient;
    return (
      <div onBlur={this.blurHandler} onFocus={this.focusHandler}>
        <div className='recipient-add'>
          <div className='three columns'>
            <input 
              type='text'
              placeholder='First Name'
              value={recipient.firstName}
              onChange={this.changeHandler.bind(this, 'firstName')}
              onKeyDown={this.keyPress}
              autoComplete='off'
              autoFocus />
            {this.state.firstNameError? <p className='error'>* required </p>: null}
          </div>
          <div className='three columns'>
            <input
              type='text'
              placeholder='Last Name'
              value={recipient.lastName}
              onChange={this.changeHandler.bind(this, 'lastName')}
              onKeyDown={this.keyPress} />
            {this.state.lastNameError? <p className='error'>* required </p>: null}
          </div>
          <div className='five columns'>
            <input
              type='email'
              placeholder='E-mail'
              value={recipient.email}
              onChange={this.changeHandler.bind(this, 'email')}
              onKeyDown={this.keyPress} />
            {this.state.emailError? <p className='error'>* invalid email</p>: null}
          </div>
          <div className='one columns'>
            <div className='recipient-button' onClick={this.updateHandler}>
              {this.props.selectedRecipient === null? <p>+</p>: <p>&#10004;</p>}
            </div>
          </div>
        </div> 
      </div>
    );
  }
});

module.exports = RecipientEdit;

