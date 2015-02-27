var React = require('react');
var _pick = require('lodash.pick');
var Suggestions = require('./RecipientSuggestions');

var RecipientEdit = React.createClass({
  getInitialState: function () {
    return {
      recipient: this.props.recipient,
      error: false,
      showSuggestions: false,
      position: {}
    }
  },
  componentWillReceiveProps: function (nextProps) {
    this.setState({
      recipient: nextProps.recipient,
      error: false
    });
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
    var recipient = this.state.recipient;
    if (recipient.firstName && recipient.lastName && recipient.email) {
      this.props.updateRecipient(recipient);
      this.setState({
        error: false,
        showSuggestions: false
      });
    } else {
      this.setState({
        error: true,
        showSuggestions: false
      });
    }
  },
  render: function () {
    var recipient = this.state.recipient;
    return (
      <div className='rcpent-add row'>
        <input className='three columns' type='text' placeholder='First Name' value={recipient.firstName} onChange={this.changeHandler.bind(this, 'firstName')} />
        <input className='three columns' type='text' placeholder='Last Name' value={recipient.lastName} onChange={this.changeHandler.bind(this, 'lastName')} />
        <input className='four columns' type='email' placeholder='E-mail' value={recipient.email} onChange={this.changeHandler.bind(this, 'email')} />
        <button className='one column' onClick={this.updateHandler}>
          <img src='images/accept.png' />
        </button> 
        {this.state.error? <p className='error'>Fill all fields</p> : null}
        <Suggestions show={this.state.showSuggestions} position={this.state.position} selectSuggestion={this.selectSuggestion} />
      </div>
    );
  }
});

module.exports = RecipientEdit;