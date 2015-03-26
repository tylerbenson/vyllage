var React = require('react');
var actions = require('../../actions');

var SkillsForm = React.createClass({
  getInitialState: function () {
    return {
      skill: ''
    };
  },
  changeHandler: function (e) {
    this.setState({
      skill: e.target.value
    });
  },
  updateSection: function (e) {
    if (this.state.skill) {
      if (this.props.selectedSkill) {
        var skill = this.props.skill;
        skill.description = this.state.skill;
        actions.putSection(skill);
      } else {
        actions.postSection({
          type: 'freeform',
          title: 'skills',
          description: this.state.skill
        });
      }
      this.setState({skill: ''});
      this.refs.skill.getDOMNode().value = '';
      this.props.setSelectedSkill(null);
    }
  },
  keyPress: function (e) {
    if (e.key === 'Enter') {
      this.updateSection(e);
    }
  },
  render: function () {
    return (
      <div className='row'>
        <input 
          ref='skill'
          type='text'
          className='five columns'
          defaultValue={this.props.skill.description} 
          onChange={this.changeHandler}
          onKeyPress={this.keyPress}
        />
        <a 
          className='button'
          onClick={this.updateSection}>
            {(this.state.selectedSkill === null) ? 'ADD': 'SAVE' }
        </a>
      </div>
    );
  }
});

module.exports = SkillsForm;