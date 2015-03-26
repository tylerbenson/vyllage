var React = require('react');

var SkillsList = React.createClass({
  render: function () {
    var sections = this.props.sections || [];
    var skillNodes = sections.map(function (section) {
      return <span>{section.description}</span>;
    });
    return (
      <div className='row'>
        {skillNodes}
      </div>
    );
  }
});

module.exports = SkillsList;