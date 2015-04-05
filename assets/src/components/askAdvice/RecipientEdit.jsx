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
        <div className='form'>
          <div className='field'>
            <label>First Name</label>
            <input 
              type='text'
              className='name'
              value={recipient.firstName}
              onChange={this.changeHandler.bind(this, 'firstName')}
              onKeyDown={this.keyPress}
              autoComplete='off'
              autoFocus />
            {this.state.firstNameError? <p className='error'>* required </p>: null}
          </div>
          <div className='field'>
            <label>Last Name</label>
            <input
              type='text'
              className='name'
              value={recipient.lastName}
              onChange={this.changeHandler.bind(this, 'lastName')}
              onKeyDown={this.keyPress} />
            {this.state.lastNameError? <p className='error'>* required </p>: null}
          </div>
          <div className='field'>
            <label>E-mail</label>
            <input
              type='email'
              className='email'
              value={recipient.email}
              onChange={this.changeHandler.bind(this, 'email')}
              onKeyDown={this.keyPress} />
            {this.state.emailError? <p className='error'>* invalid email</p>: null}
          </div>
          <div className='field'>
            <button onClick={this.updateHandler}>
              {this.props.selectedRecipient === null? <i className='ion-plus'></i> : <i className='ion-checkmark'></i>}
            </button>
          </div>
        </div> 
      </div>
    );
  }
});

module.exports = RecipientEdit;
