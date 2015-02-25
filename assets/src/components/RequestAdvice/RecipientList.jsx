var React = require('react');

var RecipentList = React.createClass({
  removeHandler: function (index, e) {
    this.props.removeRecipient(index);
  },
  editHandler: function (index, e) {
    this.props.selectRecipient(index);
  },
  render: function () {
    var nodes = this.props.recipients.map(function (recipient, index) {
      return (
        <div key={index} className="rcpent">
          <p onClick={this.editHandler.bind(this, index)}>{recipient.firstName + " " + recipient.lastName}</p>
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