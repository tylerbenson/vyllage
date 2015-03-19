var React = require('react');
var Header = require('./Header');
var FreeformContainer = require('../freeform');

var Skills = React.createClass({
  render: function () {
    var sectionNodes;
    var sections = this.props.sections || [];
    if (sections.length > 0) {
      sectionNodes = sections.map(function (section, index) {
        return <FreeformContainer key={index} freeformData={section} />
      });
    } else {
      sectionNodes = <p className='add-more'>No skills added yet</p>
    }
    return (
      <article className='career-goal'>
        <div className="row">
          <div className="twelve columns">
            <Header title='skills' type='skill' /> 
            {sectionNodes}
          </div>
        </div>
      </article>
    );
  }
});

module.exports = Skills;