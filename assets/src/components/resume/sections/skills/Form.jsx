var React = require('react');
var actions = require('../../actions');

var SkillsForm = React.createClass({
  getInitialState: function () {
    return {
      description: ''
    };
  },
  changeHandler: function (e) {
    this.setState({
      description: e.target.value
    });
  },
  componentWillReceiveProps: function (nextProps) {
    if (nextProps.selectedSkill !== null) {
      this.setState({description: nextProps.skill.description})
    }
  },
  updateSection: function (e) {
    if (this.state.description) {
      if (this.props.selectedSkill !== null) {
        var skill = this.props.skill;
        skill.description = this.state.description;
        actions.putSection(skill);
      } else {
        actions.postSection({
          type: 'freeform',
          title: 'skills',
          description: this.state.description
        });
      }
      this.props.setSelectedSkill(null);
      this.setState({description: ''});
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
          value={this.state.description} 
          onChange={this.changeHandler}
          onKeyPress={this.keyPress}
        />
        <a 
          className='button'
          onClick={this.updateSection}>
            {(this.props.selectedSkill === null) ? 'ADD': 'SAVE' }
        </a>
      </div>
    );
  }
});

module.exports = SkillsForm;