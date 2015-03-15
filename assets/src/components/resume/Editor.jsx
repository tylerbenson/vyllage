var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');
var filter = require('lodash.filter');
var CareerGoals = require('./sections/CareerGoals');
var Experience = require('./sections/Experience');
var Education = require('./sections/Education');
var Skills = require('./sections/Skills');

var ResumeEditor = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume')],
  componentDidMount: function () {
    // actions.getResume({documentId: 1});
  },
  render: function () {
    var careerGoalsSections = filter(this.state.resume.sections, {type: 'career-goals'});
    var experienceSections = filter(this.state.resume.sections, {type: 'experience'});
    var educationSections = filter(this.state.resume.sections, {type: 'education'});
    var skillsSections = filter(this.state.resume.sections, {type: 'skills'});
    return (
      <div>
        <CareerGoals sections={careerGoalsSections} />
        <Experience sections={experienceSections} />
        <Education sections={educationSections} />
        <Skills sections={skillsSections} />
      </div>
    );
  }
});

module.exports = ResumeEditor;