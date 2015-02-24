var React = require('react');
var _pick = require('lodash.pick');

var RecipientAdd = React.createClass({
  getInitialState: function () {
    return {
      firstName: "",
      lastName: "",
      email: "",
      error: false
    }
  },
  changeHandler: function (key, e) {
    e.preventDefault();
    var state = {};
    state[key] = e.target.value
    this.setState(state);
  },
  addHandler: function (e) {
    e.preventDefault();
    if (this.state.firstName && this.state.lastName && this.state.email) {
      var recipient = _pick(this.state, ['firstName', 'lastName', 'email']);
      this.props.addRecipient(recipient);
      this.setState(this.getInitialState());
    } else {
      this.setState({error: true});
    }
  },
  render: function () {
    return (
      <div className='rcpent-add row'>
        <input className='three columns' type='text' placeholder='First Name' onChange={this.changeHandler.bind(this, 'firstName')} />
        <input className='three columns' type='text' placeholder='Last Name' onChange={this.changeHandler.bind(this, 'lastName')} />
        <input className='four columns' type='email' placeholder='E-mail' onChange={this.changeHandler.bind(this, 'email')} />
        <button className='one column' onClick={this.addHandler}>
          <img src='images/accept.png' />
        </button> 
        {this.state.error? <p className='error'>Fill all fields</p> : null}
      </div>
    );
  }
});

module.exports = RecipientAdd;