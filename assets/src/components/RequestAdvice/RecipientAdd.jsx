var React = require('react');

var RecipientAdd = React.createClass({
  render: function () {
    return (
      <div className='rcpent-add row'>
          <input className='three columns' type='text' placeholder='First Name'/>
          <input className='three columns' type='text' placeholder='Last Name'/>
          <input className='four columns' type='email' placeholder='E-mail'/>
          <button className='one column' type='submit'>
            <img src='images/accept.png' />
          </button> 
      </div>
    );
  }
});

module.exports = RecipientAdd;