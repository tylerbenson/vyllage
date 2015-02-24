var React = require('react');

var RecipientAdd = React.createClass({
  getInitialState: function () {
    return this.resetState();
  },
  resetState: function () {
    return {
      firstName: "",
      lastName: "",
      email: ""
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
    this.props.addRecipient(this.state);
    this.setState(this.resetState());
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
      </div>
    );
  }
});

module.exports = RecipientAdd;