var React = require('react');
var cx = require('react/lib/cx');
var Actions = require('./actions');

var RecipentList = React.createClass({
  removeUserRecipientHandler: function (index, e) {
    e.preventDefault();
    Actions.removeUserRecipient(index);
  },
  removeNotRegisteredUserRecipientHandler: function (index, e) {
    e.preventDefault();
    Actions.removeNotRegisteredUserRecipient(index);
  },
  editHandler: function (index, e) {
    e.preventDefault();
    Actions.selectRecipient(index);
  },
  renderUserRecipients: function () {
    return this.props.users.map(function (recipient, index) {
      var classes = {
        recipient: true,
        disabled: true
      };
      return (
        <div key={recipient} className={cx(classes)}>
          <span className='name'>
            {recipient.firstName + " " + recipient.lastName}
          </span>
          <button
            className='flat icon secondary remove'
            onClick={this.removeUserRecipientHandler.bind(this, index)}>
            <i className="ion-android-close"></i>
          </button>
        </div>
      );
    }.bind(this));
  },
  renderNotRegisteredUsers: function () {
    return this.props.notRegisteredUsers.map(function (recipient, index) {
      var classes = {
        recipient: true,
        'recipient-editable': true,
      };
      return (
        <div key={recipient} className={cx(classes)}>
          <span className='name' onClick={this.editHandler.bind(this, index)}>
            {recipient.firstName + " " + recipient.lastName}
          </span>
          <button
            className='flat icon secondary remove'
            onClick={this.removeNotRegisteredUserRecipientHandler.bind(this, index)}>
            <i className="ion-android-close"></i>
          </button>
        </div>
      );
    }.bind(this));
  },
  render: function () {
    return (
      <div className='header recipients'>
        <h2 className="secondary">List of Recipients</h2>
        <div className='list'>
          {this.renderUserRecipients()}
          {this.renderNotRegisteredUsers()}
          {this.props.users.length || this.props.notRegisteredUsers.length? null: <div className="empty">No recipients yet. Start adding people below.</div>}
        </div>
      </div>
    );
  }
});

module.exports = RecipentList;