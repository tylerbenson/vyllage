var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');
var filter = require('lodash.filter');
var CareerGoals = require('./sections/CareerGoals');
var Experience = require('./sections/Experience');
var Education = require('./sections/Education');
var Skills = require('./sections/Skills');
var Profile = require('../profile');
var Contact = require('../contact/contact');
var Share = require('../contact/share');

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
        <article className='profile' id='profile'>
          <Profile />
        </article>
        <article className="info-sections" id='share-info'>
          <Share />
        </article>
        <article className="info-sections" id='contact-info'>
          <Contact />
        </article>
        <CareerGoals sections={careerGoalsSections} />
        <Experience sections={experienceSections} />
        <Education sections={educationSections} />
        <Skills sections={skillsSections} />
      </div>
    );
  }
});

module.exports = ResumeEditor;