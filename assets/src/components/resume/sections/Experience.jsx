var React = require('react');
var Header = require('./Header');
var Organization = require('../organization');
var SectionFooter = require('./Footer');

var Experience = React.createClass({
  render: function () {
    var sectionNodes;
    var sections = this.props.sections || [];
    if (sections.length > 0) {
      sectionNodes = sections.map(function (section, index) {
        return (
          <div key={index} >
            <Organization section={section}/>
            <SectionFooter section={section} />
          </div>
        );
      })
    } else {
      sectionNodes = <p className='content empty'>No experience added yet</p>;
    }
    return (
      <div className='section'>
        <div className='container'>
          <Header title='Experience' />
          {sectionNodes}
        </div>
      </div>
    );
  }
});

module.exports = Experience;