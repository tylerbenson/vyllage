var React = require('react');
var _pick = require('lodash.pick');

var RecipientEdit = React.createClass({
  getInitialState: function () {
    return {
      recipient: this.props.recipient,
      error: false
    }
  },
  componentWillReceiveProps: function (nextProps) {
    this.setState({
      recipient: nextProps.recipient,
      error: false
    });
  },
  changeHandler: function (key, e) {
    e.preventDefault();
    var recipient = this.state.recipient;
    recipient[key] = e.target.value
    this.setState({recipient: recipient});
  },
  updateHandler: function (e) {
    e.preventDefault();
    var recipient = this.state.recipient;
    if (recipient.firstName && recipient.lastName && recipient.email) {
      this.props.updateRecipient(recipient);
      this.setState({error: false});
    } else {
      this.setState({error: true});
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
      </div>
    );
  }
});

module.exports = RecipientEdit;