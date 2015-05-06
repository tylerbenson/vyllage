var React = require('react');
var Header = require('./Header');
var Organization = require('../organization');
var SectionFooter = require('./Footer');
var MoveButton = require('../../buttons/move');
var  { DragDropMixin } = require('react-dnd');
var {dragSource, dropTarget} = require('./sectionDragDrop');

var Education = React.createClass({
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
    var placeholders = {
      role: "Degree",
      roleDescription: "Field of study",
      highlights: "Add at least three highlights of your education"
    };
    var sectionNodes
    var sections = this.props.sections || [];
    if (sections.length > 0) {
      sectionNodes = sections.map(function (section, index) {
        return (
          <div key={section.sectionId}>
            <Organization 
              section={section} 
              placeholders={placeholders} 
              owner={this.props.owner}
              moveSubSection={this.props.moveSubSection} 
            />
            <SectionFooter section={section} />
          </div>
        );
      }.bind(this))
    } else {
      sectionNodes = <p className='empty content'>No education added yet</p>
    }
    return (
      <div className='section' {...this.dropTargetFor('section')}>
        <div className='container'>
          <MoveButton {...this.dragSourceFor('section')} />
          <Header title='Education' type='experience' owner={this.props.owner}/>
          {sectionNodes}
        </div>
      </div>
    );
  }
});

module.exports = Education;