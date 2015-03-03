var React = require('react');
var _pick = require('lodash.pick');
var Suggestions = require('./RecipientSuggestions');
var validator = require('validator');

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
    this.props.onChange(key, e.target.value);
  },
  updateHandler: function (e) {
    e.preventDefault();
    var isValid = this.validate();
    var recipient = this.props.recipient;
    if (isValid) {
      this.props.onSubmit(recipient);
    }
  },
  render: function () {
    var recipient = this.props.recipient;
    return (
      <div onBlur={this.props.closeSuggestions} onFocus={this.props.openSuggestions}>
        <div className='rcpent-add'>
          <div className='three columns'>
            <input  type='text' placeholder='First Name' value={recipient.firstName} onChange={this.changeHandler.bind(this, 'firstName')} />
            {this.state.firstNameError? <p className='error'>* required </p>: null}
          </div>
          <div className='three columns'>
            <input  type='text' placeholder='Last Name' value={recipient.lastName} onChange={this.changeHandler.bind(this, 'lastName')} />
            {this.state.lastNameError? <p className='error'>* required </p>: null}

          </div>
          <div className='five columns'>
            <input type='email' placeholder='E-mail' value={recipient.email} onChange={this.changeHandler.bind(this, 'email')} />
            {this.state.emailError? <p className='error'>* invalid email</p>: null}
          </div>
          <div className='one columns'>
            <a className='add-button'><img src='images/add.png' onClick={this.updateHandler} /></a>
          </div>
        </div> 
      </div>
    );
  }
});

module.exports = RecipientEdit;

// <Suggestions
//             show={this.state.showSuggestions}
//             position={this.state.position}
//             selectSuggestion={this.selectSuggestion}
//           />