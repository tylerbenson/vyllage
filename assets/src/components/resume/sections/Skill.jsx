var React = require('react');
var Header = require('./Header');
var Freeform = require('../freeform');
var SectionFooter = require('./Footer');

var Skills = React.createClass({
  render: function () {
    var sectionNodes;
    var sections = this.props.sections || [];
    if (sections.length > 0) {
      sectionNodes = sections.map(function (section, index) {
        return (
          <div key={index}>
            <Freeform title='Skills'  section={section} />
            <SectionFooter section={section} />
          </div>
        )
      });
    } else {
      sectionNodes = <p className='add-more'>No skills added yet</p>
    }
    return (
      <div className='section'>
        <div className="content">
          <Header title='Skills' /> 
          {sectionNodes}
        </div>
      </div>
    );
  }
});

module.exports = Skills;