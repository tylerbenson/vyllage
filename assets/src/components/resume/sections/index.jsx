var React = require('react');
var Freeform = require('../freeform');
var Tags = require('../tags');
var Organization = require('../organization');
var sections = require('../../sections');
var filter = require('lodash.filter');

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
    var sectionSpec = filter(sections, {type: section.type});
    var isMultiple = sectionSpec instanceof Array ? sectionSpec[0].isMultiple : false;

    switch(section.type) {
      case 'SkillsSection':
      case 'CareerInterestsSection':
        return (
          <Tags
            index={this.props.index}
            section={section}
            owner={this.props.owner}
            isMultiple={isMultiple} />
        );
      case 'SummarySection':
        return (
          <Freeform
            index={this.props.index}
            section={section}
            owner={this.props.owner}
            isMultiple={isMultiple} />
        );
      case 'EducationSection':
      case 'JobExperienceSection':
        return (
          <Organization
            index={this.props.index}
            section={section}
            placeholders={this.getPlaceholders()}
            owner={this.props.owner}
            isMultiple={isMultiple} />
        );
      default:
        return null; //does not render unsupported section
    }
  }
});

module.exports = Section;