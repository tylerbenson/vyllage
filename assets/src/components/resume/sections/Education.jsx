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
            <SectionFooter />
          </div>
        );
      })
    } else {
      sectionNodes = <p className='add-more'>No education added yet</p>
    }
    return (
      <article className='experience'>
        <div className="row">
          <div className="twelve columns">
            <Header title='Education' /> 
            {sectionNodes}
          </div>
        </div>
      </article>
    );
  }
});

module.exports = Education;