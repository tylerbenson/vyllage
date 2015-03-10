var React = require('react');
var OtherItem = require('./OtherItem');

var OtherList = React.createClass({
  render: function () {
    var otherNodes = this.props.others.map(function (other, index) {
      return <OtherItem 
              {...other}
              organization={this.props.organization}
              index={index}
              key={index}
            />
    }.bind(this)); 
    return (
      <ul>
        {otherNodes}
      </ul>
    );
  }
});

module.exports = OtherList;