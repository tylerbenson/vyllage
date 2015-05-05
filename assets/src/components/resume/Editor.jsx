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
  },
  render: function () {
    var owner=this.state.resume.header.owner;
    var careerGoalSections = filter(this.state.resume.sections, {title: 'career goal'});
    var skillSections = filter(this.state.resume.sections, {title: 'skills'});
    var experienceSections = sortby(filter(this.state.resume.sections, {title: 'experience'}), 'sectionPostion').reverse();
    var educationSections = sortby(filter(this.state.resume.sections, {title: 'education'}), 'sectionPostion').reverse();
    return (
      <div>
        {owner ? <Subheader documentId={this.state.resume.documentId}/>: null}
        <Banner header={this.state.resume.header} settings={this.state.settings} />
        <div className="sections">
          <CareerGoal section={careerGoalSections[0]} owner={owner} />
          <Experience sections={experienceSections} owner={owner} />
          <Education sections={educationSections} owner={owner} />
          <Skill section={skillSections[0]} owner={owner} />
        </div>
      </div>
    );
  }
});

module.exports = ResumeEditor;