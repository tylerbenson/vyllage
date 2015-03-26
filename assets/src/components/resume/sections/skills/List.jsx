var React = require('react');

var SkillsList = React.createClass({
  render: function () {
    var sections = this.props.sections || [];
    var skillNodes = sections.map(function (section) {
      return (
        <div>
          <span className='skill-item'>{section.description}</span>
          <i className='icon ion-close'></i>
        </div>
      );
    });
    return (
      <div className='u-pull-left'>
        {skillNodes}
      </div>
    );
  }
});

module.exports = SkillsList;