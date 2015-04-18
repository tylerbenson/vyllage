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
  render: function () {
    var careerGoalSections = filter(this.state.resume.sections, {title: 'career goal'});
    var skillSections = filter(this.state.resume.sections, {title: 'skills'});
    var experienceSections = sortby(filter(this.state.resume.sections, {title: 'experience'}), 'sectionPostion').reverse();
    var educationSections = sortby(filter(this.state.resume.sections, {title: 'education'}), 'sectionPostion').reverse();
    return (
      <div>
        <Subheader documentId={this.state.resume.documentId}/>
        <Banner header={this.state.resume.header} />
        <div className="sections">
          <CareerGoal section={careerGoalSections[0]} owner={this.state.resume.header.owner} />
          <Experience sections={experienceSections} owner={this.state.resume.header.owner} />
          <Education sections={educationSections} owner={this.state.resume.header.owner} />
          <Skill section={skillSections[0]} owner={this.state.resume.header.owner} />
        </div>
      </div>
    );
  }
});

module.exports = ResumeEditor;