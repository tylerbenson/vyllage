var React = require('react');
var Header = require('./Header');
var Organization = require('../organization');
var SectionFooter = require('./Footer');

var Education = React.createClass({
  render: function () {
    var sectionNodes
    var sections = this.props.sections || [];
    if (sections.length > 0) {
      sectionNodes = sections.map(function (section, index) {
        return (
          <div key={index}>
            <Organization section={section}/>
            <SectionFooter section={section} />
          </div>
        );
      })
    } else {
      sectionNodes = <p className='add-more'>No education added yet</p>
    }
    return (
      <div className='section'>
        <Header title='Education' /> 
        {sectionNodes}
      </div>
    );
  }
});

module.exports = Education;