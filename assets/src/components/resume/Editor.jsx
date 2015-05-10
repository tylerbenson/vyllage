var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');
var settingStore = require('../settings/store');
var filter = require('lodash.filter');
var Subheader = require('./Subheader');
var Header = require('./sections/Header');
var Section = require('./sections');
var Banner = require('./banner');
var sortby = require('lodash.sortby');
var EmptySections = require('./sections/Empty');

var ResumeEditor = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume'), Reflux.connect(settingStore)],
  componentWillMount: function () {
    actions.getResume();
  },
  moveSection: function (index, afterIndex) {
    // var { sections } = this.state.resume;
    // console.log()
    // var section = sections.splice(index, 1);
    // sections.splice(afterIndex, 0, section);
    // actions.updateSectionOrder(sections);
    actions.moveSection(index, afterIndex);
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
          index={index}
          key={section.sectionId}
          section={section}
          moveSection={this.moveSection}
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
    }.bind(this));
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
          {owner ? <EmptySections sections={this.state.resume.sections} owner={owner} />: null}
        </div>
      </div>
    );
  }
});

module.exports = ResumeEditor;