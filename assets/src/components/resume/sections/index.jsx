var React = require('react');
var Freeform = require('../freeform');
var Tags = require('../tags');
var Organization = require('../organization');

var Section = React.createClass({
  getPlaceholders: function () {
    if (this.props.section.title === 'education') {
      return {
        role: "Degree",
        roleDescription: "Field of study",
        highlights: "Add at least three highlights of your education"
      };
    } else if (this.props.section.title === 'experience') {
      return {
        role: 'Position'
      };
    }
  },
  render: function () {
    var section = this.props.section;
    switch(section.type) {
      case 'SkillsSection':
      case 'CareerInterestsSection':
        return (
          <Tags
            index={this.props.index}
            section={section}
            moveSection={this.props.moveSection}
            owner={this.props.owner} />
        );
      case 'SummarySection':
        return (
          <Freeform
            index={this.props.index}
            section={section}
            moveSection={this.props.moveSection}
            owner={this.props.owner} />
        );
      case 'EducationSection':
      case 'JobExperienceSection':
        return (
          <Organization
            index={this.props.index}
            section={section}
            moveSection={this.props.moveSection}
            placeholders={this.getPlaceholders()}
            owner={this.props.owner} />
        );
      default:
        return null; //does not render unsupported section
    }
  }
});

module.exports = Section;