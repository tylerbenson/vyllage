var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');
var settingStore = require('../settings/store');
var filter = require('lodash.filter');
var Subheader = require('./Subheader');
var CareerGoal = require('./sections/CareerGoal');
var Experience = require('./sections/Experience');
var Education = require('./sections/Education');
var Skill = require('./sections/Skill');
var Banner = require('./banner');
var sortby = require('lodash.sortby');

var ResumeEditor = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume'), Reflux.connect(settingStore)],
  componentWillMount: function () {
    actions.getResume();
  },
  moveSection: function (title, afterTitle) {
    const { sectionOrder } = this.state.resume;
    const section = sectionOrder.filter(c => c === title)[0];
    const afterSection = sectionOrder.filter(c => c === afterTitle)[0];
    const sectionIndex = sectionOrder.indexOf(section);
    const afterIndex = sectionOrder.indexOf(afterSection);
    sectionOrder.splice(sectionIndex, 1);
    sectionOrder.splice(afterIndex, 0, section);
    actions.updateSectionOrder(sectionOrder);
    console.log(sectionOrder);
  },
  moveSubSection: function (title, afterTitle) {
    const { sectionOrder } = this.state.resume;
    const section = sectionOrder.filter(c => c === title)[0];
    const afterSection = sectionOrder.filter(c => c === afterTitle)[0];
    const sectionIndex = sectionOrder.indexOf(section);
    const afterIndex = sectionOrder.indexOf(afterSection);
    sectionOrder.splice(sectionIndex, 1);
    sectionOrder.splice(afterIndex, 0, section);
    actions.updateSectionOrder(sectionOrder);
    console.log(sectionOrder);
  },
  render: function () {
    var owner=this.state.resume.header.owner;
    var careerGoalSections = filter(this.state.resume.sections, {title: 'career goal'});
    var skillSections = filter(this.state.resume.sections, {title: 'skills'});
    var experienceSections = sortby(filter(this.state.resume.sections, {title: 'experience'}), 'sectionPostion').reverse();
    var educationSections = sortby(filter(this.state.resume.sections, {title: 'education'}), 'sectionPostion').reverse();
    var sections = this.state.resume.sectionOrder.map(function (title, index) {
      if (title === 'career goal') {
        return <CareerGoal 
            key={title}
            title='career goal'
            section={careerGoalSections[0]}
            owner={owner}
            moveSection={this.moveSection} />;
      } else if (title === 'experience') {
        return <Experience 
            key={title}
            title='experience'
            sections={experienceSections}
            owner={owner} 
            moveSection={this.moveSection} 
            moveSubSection={this.moveSubSection} 
            />;
      } else if (title === 'education') {
        return <Education 
            key={title}
            title='education'
            sections={educationSections} 
            owner={owner} 
            moveSection={this.moveSection}
            moveSubSection={this.moveSubSection}
             />;
      } else {
        return <Skill 
            key={title}
            title='skills'
            section={skillSections[0]} 
            owner={owner} 
            moveSection={this.moveSection} />;
      }
    }.bind(this))
    return (
      <div>
        {owner ? <Subheader documentId={this.state.resume.documentId}/>: null}
        <Banner header={this.state.resume.header} settings={this.state.settings} />
        <div className="sections">
          {sections}
        </div>
      </div>
    );
  }
});

module.exports = ResumeEditor;