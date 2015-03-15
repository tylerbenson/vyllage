var React = require('react');
var Header = require('./Header');
var FreeformContainer = require('../../freeform/container');

var CareerGoal = React.createClass({
  render: function () {
    var sections = this.props.sections || [];
    var sectionNodes = sections.map(function (section, index) {
      return <FreeformContainer  key={index} freeformData={section} ref="section"/>
    });
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