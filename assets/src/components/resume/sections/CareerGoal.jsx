var React = require('react');
var actions = require('../actions');
var Freeform = require('../freeform');
var SectionFooter = require('./Footer');

var CareerGoal = React.createClass({
  render: function () {
    return (
      <div className='resume-section'>
        <div>
          <Freeform title='Career Goal' section={this.props.careerGoal} />
          <SectionFooter section={this.props.careerGoal} />
        </div>
      </div>
    );
  }
});

module.exports = CareerGoal;