var React = require('react');
var SkillList = require('./List');
var SkillForm = require('./Form');

var Skills = React.createClass({
  render: function () {
    return (
      <div className='resume-section'>
        <h4 className='resume-section-title'>Skills</h4> 
        <SkillList {...this.props}/>
        <SkillForm {...this.props}/>
      </div>
    );
  }
});

module.exports = Skills;