var React = require('react');
var actions = require('../../actions');

var SkillsList = React.createClass({
  deleteSkill: function (sectionId, e) {
    actions.deleteSection(sectionId);
  },
  select: function (index) {
    this.props.setSelectedSkill(index);
  },
  render: function () {
    var sections = this.props.sections || [];
    var skillNodes = sections.map(function (section, index) {
      return (
        <div key={index} className='skill-item u-float-left'>
          <span 
            onClick={this.select.bind(this, index)}
          >{section.description}</span>
          <span 
            className='icon ion-close'
            onClick={this.deleteSkill.bind(this, section.sectionId)}></span>
        </div>
      );
    }.bind(this));
    return (
      <div className='row'>
        {skillNodes}
      </div>
    );
  }
});

module.exports = SkillsList;