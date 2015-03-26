var React = require('react');
var SkillList = require('./List');
var SkillForm = require('./Form');

var Skills = React.createClass({
  getInitialState: function () {
    return { selectedSkill: null };
  },
  setSelectedSkill: function (index) {
    this.setState({selectedSkill: index});
  },
  render: function () {
    var skill = (this.state.selectedSkill !== null) ? this.props.sections[this.state.selectedSkill]: {description: ''};
    console.log(skill, this.state.selectedSkill)
    return (
      <div className='resume-section'>
        <h4 className='resume-section-title'>Skills</h4> 
        <SkillList {...this.props} setSelectedSkill={this.setSelectedSkill} />
        <SkillForm 
          {...this.props} 
          skill={skill}
          setSelectedSkill={this.setSelectedSkill}
          selectedSkill={this.state.selectedSkill} />
      </div>
    );
  }
});

module.exports = Skills;