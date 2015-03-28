var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');
var filter = require('lodash.filter');
var Subheader = require('./Subheader');
var CareerGoal = require('./sections/CareerGoal');
var Experience = require('./sections/Experience');
var Education = require('./sections/Education');
var Skill = require('./sections/Skill');
var Banner = require('./banner');

var ResumeEditor = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume')],
  componentWillMount: function () {
    actions.getResume();
  },
  render: function () {
    var careerGoalSections = filter(this.state.resume.sections, {title: 'career goal'});
    // There will be only one career goal section.
    var careerGoal = careerGoalSections[0];

    var experienceSections = filter(this.state.resume.sections, {title: 'experience'});
    var educationSections = filter(this.state.resume.sections, {title: 'education'});
    var skillSections = filter(this.state.resume.sections, {title: 'skills'});
    var profileData = this.state.resume.header;
    var contactData = this.state.resume.contact;
    return (
      <div>
        <Subheader documentId={this.state.resume.documentId}/>
        <Banner profileData={profileData} contactData={contactData}/>
        <section className="container">
          <CareerGoal careerGoal={careerGoal} />
          <Experience sections={experienceSections} />
          <Education sections={educationSections} />
          <Skill sections={skillSections} />
        </section>
      </div>
    );
  }
});

module.exports = ResumeEditor;