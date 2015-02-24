var React = require('react');

var RecipentList = React.createClass({
  removeHandler: function (index, e) {
    this.props.removeRecipient(index);
  },
  render: function () {
    var nodes = this.props.recipients.map(function (recipient, index) {
      return (
          <div className="rcpent">
            <span key={index}>{recipient.firstName + " " + recipient.lastName}</span>
            <a onClick={this.removeHandler.bind(this, index)}>x</a>
          </div>
      );
    }.bind(this));
    return (
      <div className='rcpent-list'>
        {nodes}
      </div>
    );
  }
});

module.exports = RecipentList;