var React = require('react');
var actions = require('../actions');
var Freeform = require('../freeform');

var CareerGoal = React.createClass({
  render: function () {
    return (
      <article className='career-goal'>
        <div>
          <Freeform title='Career Goal' section={this.props.careerGoal} />
        </div>
      </article>
    );
  }
});

module.exports = CareerGoal;