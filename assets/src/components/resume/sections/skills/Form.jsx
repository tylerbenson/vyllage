var React = require('react');

var SkillsForm = React.createClass({
  render: function () {
    return (
      <div className='row'>
        <input type='text' className='five columns' />
        <a className='button'>ADD</a>
      </div>
    );
  }
});

module.exports = SkillsForm;