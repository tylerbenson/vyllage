var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');
var filter = require('lodash.filter');
var CareerGoal = require('./sections/CareerGoal');
var Experience = require('./sections/Experience');
var Education = require('./sections/Education');
var Skill = require('./sections/Skill');
var Header = require('./header/Header');

var ResumeEditor = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume')],
  componentDidMount: function () {
    // actions.getResume({documentId: 1});
  },
  render: function () {
    var careerGoalSections = filter(this.state.resume.sections, {type: 'career-goal'});
    var experienceSections = filter(this.state.resume.sections, {type: 'experience'});
    var educationSections = filter(this.state.resume.sections, {type: 'education'});
    var skillSections = filter(this.state.resume.sections, {type: 'skill'});
    var profileData = this.state.resume.header;
    var contactData = this.state.resume.contact;
    console.log(this.state.resume)
    return (
      <div>
        <Header profileData={profileData} contactData={contactData}/>
        <section className="container">
          <CareerGoal sections={careerGoalSections} />
          <Experience sections={experienceSections} />
          <Education sections={educationSections} />
          <Skill sections={skillSections} />
        </section>
      </div>
    );
  }
});

module.exports = ResumeEditor;