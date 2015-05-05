var React = require('react');
var actions = require('../actions');
var Header = require('./Header');
var Freeform = require('../freeform');
var SectionFooter = require('./Footer');
var MoveButton = require('../../buttons/move');
var  { DragDropMixin } = require('react-dnd');
var {dragSource, dropTarget} = require('./sectionDragDrop');

var CareerGoal = React.createClass({
  mixins: [DragDropMixin],
  statics: {
    configureDragDrop(register) {
      register('section', {
        dragSource,
        dropTarget
      });
    }
  },
  render: function () {
    return (
      <div className='section' {...this.dropTargetFor('section')}>
        <div className='container'>
          <MoveButton {...this.dragSourceFor('section')} />
          <Freeform title='Career Goal' section={this.props.section} owner={this.props.owner}/>
        </div>
      </div>
    );
  }
});

module.exports = CareerGoal;