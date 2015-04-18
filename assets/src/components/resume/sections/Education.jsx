var React = require('react');
var Header = require('./Header');
var Organization = require('../organization');
var SectionFooter = require('./Footer');

var Education = React.createClass({
  render: function () {
    var placeholders = {
      role: "Degree"
    };
    var sectionNodes
    var sections = this.props.sections || [];
    if (sections.length > 0) {
      sectionNodes = sections.map(function (section, index) {
        return (
          <div key={section.sectionId}>
            <Organization section={section} placeholders={placeholders} owner={this.props.owner} />
            <SectionFooter section={section} />
          </div>
        );
      }.bind(this))
    } else {
      sectionNodes = <p className='empty content'>No education added yet</p>
    }
    return (
      <div className='section'>
        <div className='container'>
          <Header title='Education' type='experience' />
          {sectionNodes}
        </div>
      </div>
    );
  }
});

module.exports = Education;