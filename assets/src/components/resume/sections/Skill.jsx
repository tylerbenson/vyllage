var React = require('react');
var Header = require('./Header');
var Freeform = require('../freeform');
var MoveButton = require('../../buttons/move');
var { DragDropMixin } = require('react-dnd');
var {dragSource, dropTarget} = require('./sectionDragDrop');

var Skills = React.createClass({
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
      <div className='section'>
        <div className="container">
          <MoveButton {...this.dragSourceFor('section')} />
          <Freeform title='Skills' section={this.props.section} owner={this.props.owner} />
        </div>
      </div>
    );
  }
});

module.exports = Skills;