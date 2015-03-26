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
    actions.postSection({
      type: 'freeform',
      title: 'skills',
      description: 'skill'
    });
  },
  render: function () {
    return (
      <div className='row'>
        <input type='text' className='five columns' />
        <a className='button' onClick={this.addSection}>ADD</a>
      </div>
    );
  }
});

module.exports = SkillsForm;