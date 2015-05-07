var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');
var settingStore = require('../settings/store');
var filter = require('lodash.filter');
var Subheader = require('./Subheader');
var CareerGoal = require('./sections/CareerGoal');
var Experience = require('./sections/Experience');
var Education = require('./sections/Education');
var Skill = require('./sections/Skill');
var Header = require('./sections/Header');
var Section = require('./sections');
var Banner = require('./banner');
var sortby = require('lodash.sortby');

var ResumeEditor = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume'), Reflux.connect(settingStore)],
  componentWillMount: function () {
    actions.getResume();
  },
  moveSection: function (title, afterTitle) {
    const { sectionOrder } = this.state.resume;
    const section = sectionOrder.filter(c => c === title)[0];
    const afterSection = sectionOrder.filter(c => c === afterTitle)[0];
    const sectionIndex = sectionOrder.indexOf(section);
    const afterIndex = sectionOrder.indexOf(afterSection);
    sectionOrder.splice(sectionIndex, 1);
    sectionOrder.splice(afterIndex, 0, section);
    actions.updateSectionOrder(sectionOrder);
    console.log(sectionOrder);
  },
  renderSections: function () {
    var owner=this.state.resume.header.owner;
    var sectionNodes = []
    var subsectionNodes = [];
    var sections = this.state.resume.sections || [];
    var previousTitle = '';
    sections.forEach(function (section, index) {
      var subsectionNode = ( 
        <Section
          key={section.sectionId}
          section={section}
          owner={owner}
        />  
      );
      
      if (previousTitle === section.title) {
        subsectionNodes.push(subsectionNode);
      } else {
        subsectionNodes = [subsectionNode];
      }

      if ((previousTitle !== section.title) || (sections.length-1 === index)) {
        sectionNodes.push(
          <div key={Math.random()} className='section'>
            <div className='container'>
              <Header title={section.title} type={section.type} owner={owner} />
              {subsectionNodes}
            </div>
          </div>
        );
      }

      previousTitle = section.title;
    });
    return sectionNodes;
  },
  render: function () {
    var owner=this.state.resume.header.owner;
    return (
      <div>
        {owner ? <Subheader documentId={this.state.resume.documentId}/>: null}
        <Banner header={this.state.resume.header} settings={this.state.settings} />
        <div className="sections">
          {this.renderSections()}
        </div>
      </div>
    );
  }
});

module.exports = ResumeEditor;