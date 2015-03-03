var React = require('react');

var RecipentList = React.createClass({
  removeHandler: function (index, e) {
    e.preventDefault();
    this.props.removeRecipient(index);
  },
  editHandler: function (index, e) {
    e.preventDefault();
    var recipient = this.props.recipients[index];
    if (recipient.newRecipient) {
      this.props.selectRecipient(index);
    }
  },
  render: function () {
    var nodes = this.props.recipients.map(function (recipient, index) {
      return (
        <div key={index} className="rcpent">
          <a onClick={this.editHandler.bind(this, index)}>{recipient.firstName + " " + recipient.lastName}</a>
          <a onClick={this.removeHandler.bind(this, index)}>x</a>
        </div>
      );
    }.bind(this));
    return (
      <div className='rcpent-list u-cf'>
        {nodes}
      </div>
    );
  }
});

module.exports = RecipentList;