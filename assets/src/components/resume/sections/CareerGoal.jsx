var React = require('react');
var actions = require('../actions');
var Header = require('./Header');
var Freeform = require('../freeform');
var SectionFooter = require('./Footer');
var MoveButton = require('../../buttons/move');

var CareerGoal = React.createClass({
  render: function () {
    return (
      <div className='section'>
        <div className='container'>
          <MoveButton />
          <Freeform title='Career Goal' section={this.props.section} owner={this.props.owner}/>
        </div>
      </div>
    );
  }
});

module.exports = CareerGoal;