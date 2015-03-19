var React = require('react');
var actions = require('../actions');
var Freeform = require('../freeform');
var SectionFooter = require('./Footer');

var CareerGoal = React.createClass({
  render: function () {
    return (
      <article className='career-goal'>
        <div>
          <Freeform title='Career Goal' section={this.props.careerGoal} />
          <SectionFooter />
        </div>
      </article>
    );
  }
});

module.exports = CareerGoal;