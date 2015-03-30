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
            <Freeform section={section} />
            <SectionFooter section={section} />
          </div>
        )
      });
    } else {
      sectionNodes = <p className='content'>No skills added yet</p>
    }
    return (
      <div className='section'>
        <div className="container">
          <Header title='Skills' /> 
          {sectionNodes}
        </div>
      </div>
    );
  }
});

module.exports = Skills;