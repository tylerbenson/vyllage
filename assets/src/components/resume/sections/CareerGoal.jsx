var React = require('react');
var Header = require('./Header');
var FreeformContainer = require('../freeform/container');

var CareerGoal = React.createClass({
  render: function () {
    var sectionNodes;
    var sections = this.props.sections || [];
    if (sections.length > 0) {
      sectionNodes = sections.map(function (section, index) {
        return <FreeformContainer key={index} freeformData={section} />
      });
    } else {
      sectionNodes = <p className='add-more'>No career goals added yet</p>
    }
    return (
      <article className='career-goal forceEditMode'>
        <div className="row">
          <div className="twelve columns">
            <Header title='career goal' type='career-goal' /> 
            {sectionNodes}
          </div>
        </div>
      </article>
    );
  }
});

module.exports = CareerGoal;