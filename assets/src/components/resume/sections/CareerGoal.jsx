var React = require('react');
var actions = require('../actions');
var Freeform = require('../freeform');

var CareerGoal = React.createClass({
  render: function () {
    console.log(this.props.careerGoal)
    return (
      <Freeform section={this.props.careerGoal} />
    );
  }
});

module.exports = CareerGoal;