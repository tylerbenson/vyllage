var React = require('react');
var Header = require('./Header');
var Freeform = require('../freeform');

var Skills = React.createClass({
  render: function () {
    var sectionNodes;
    var sections = this.props.sections || [];
    if (sections.length > 0) {
      sectionNodes = sections.map(function (section, index) {
        return <Freeform key={index} section={section} />
      });
    } else {
      sectionNodes = <p className='add-more'>No skills added yet</p>
    }
    return (
      <article className='career-goal'>
        <div className="row">
          <div className="twelve columns">
            <Header title='Skills' /> 
            {sectionNodes}
          </div>
        </div>
      </article>
    );
  }
});

module.exports = Skills;