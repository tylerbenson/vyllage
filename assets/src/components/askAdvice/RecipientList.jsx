var React = require('react');
var cx = require('react/lib/cx');
var Actions = require('./actions');

var RecipentList = React.createClass({
  removeHandler: function (index, e) {
    e.preventDefault();
    Actions.removeRecipient(index);
  },
  editHandler: function (index, e) {
    e.preventDefault();
    Actions.selectRecipient(index);
  }, 
  render: function () {
    var nodes = this.props.recipients.map(function (recipient, index) {
      var classes = {
        recipient: true,
        'recipient-selected': this.props.selectedRecipient === index,
        'recipient-editable': recipient.newRecipient 
      };
      return (
        <div key={index} className={cx(classes)}>
          <span className='name' onClick={this.editHandler.bind(this, index)}>
            {recipient.firstName + " " + recipient.lastName}
          </span>
          <button
            className='flat icon secondary remove' 
            onClick={this.removeHandler.bind(this, index)}>
            <i className="ion-android-close"></i>
          </button>
        </div>
      );
    }.bind(this));
    return (
      <div className='header recipients'>
        <h2 className="secondary">List of Recipients</h2>
        <div className='list'>
          {nodes}
        </div>
      </div>
    );
  }
});

module.exports = RecipentList;