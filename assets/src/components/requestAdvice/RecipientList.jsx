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
          <a onClick={this.editHandler.bind(this, index)}>{recipient.firstName + " " + recipient.lastName}</a>
          <a onClick={this.removeHandler.bind(this, index)} className='recipient-delete'>x</a>
        </div>
      );
    }.bind(this));
    return (
      <div className='recipient-list u-cf'>
        {nodes}
      </div>
    );
  }
});

module.exports = RecipentList;