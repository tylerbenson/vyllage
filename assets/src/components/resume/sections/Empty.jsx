var React = require('react');
var Header = require('./Header');
var filter = require('lodash.filter');

var EmptySections = React.createClass({
  render: function () {
    var sectionOptions = [
      { title: 'summary', type: 'SummarySection' },
      { title: 'experience', type: 'JobExperienceSection' },
      { title: 'education', type: 'EducationSection' },
      { title: 'skills', type: 'SkillsSection' },
      { title: 'career interests', type: 'CareerInterestsSection' }
    ];

     // { title: 'achievements', type: 'AchievementsSection' },
     // { title: 'personal references', type: 'PersonalReferencesSection' },
     // { title: 'professional references', type: 'ProfessionalReferencesSection' },
     // { title: 'projects', type: 'ProjectsSection' },
    
    var groupPosition = this.props.sections.length + 1|| 1;
    var emptyNodes = sectionOptions.map(function (options, index) {
      var sections = filter(this.props.sections, {title: options.title})
      if (sections.length === 0) {
        return (
          <div key={Math.random()} className='section'>
            <div className='container'>
              <Header title={options.title} type={options.type} owner={this.props.owner} groupPosition={groupPosition} />
              <p className='empty content'>No {options.title} added yet</p>
            </div>
          </div>
        );
        // Increment groupPosition for sections types that are empty
        groupPosition += 1;
      }
    }.bind(this));
    return (
      <div>
        {emptyNodes}
      </div>
    );
  }
});

module.exports = EmptySections;