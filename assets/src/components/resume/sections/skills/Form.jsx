var React = require('react');
var actions = require('../../actions');

var SkillsForm = React.createClass({
  getInitialState: function () {
    return {
      skill: ''
    };
  },
  changeHandler: function (e) {
    return this.setState({
      skill: e.target.value
    });
  },
  addSection: function (e) {
    if (this.state.skill) {
      actions.postSection({
        type: 'freeform',
        title: 'skills',
        description: this.state.skill
      });
      this.setState({skill: ''});
    }
  },
  keyPress: function (e) {
    if (e.key === 'Enter') {
      this.addSection(e);
    }
  },
  render: function () {
    return (
      <div className='row'>
        <input 
          type='text'
          className='five columns'
          value={this.state.skill}
          onChange={this.changeHandler}
          onKeyPress={this.keyPress}
        />
        <a className='button' onClick={this.addSection}>ADD</a>
      </div>
    );
  }
});

module.exports = SkillsForm;