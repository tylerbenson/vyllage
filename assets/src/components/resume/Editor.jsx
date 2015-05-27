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
  moveSection: function (id, afterId) {
    actions.moveSection(id, afterId);
  },
  renderGroup: function (sections) {
    var owner=this.state.resume.header.owner;
    var subsectionNodes = sections.map(function(section) {
      return <Section
          key={section.sectionId}
          section={section}
          moveSection={this.moveSection}
          owner={owner} />  
    }.bind(this));
    return (
      <div key={Math.random()} className='section'>
        <div className='container'>
          <Header 
            title={sections[0].title}
            type={sections[0].type}
            owner={owner}
            sections={sections} />
          {subsectionNodes}
        </div>
      </div>
    )
  },
  renderSections: function () {
    var owner=this.state.resume.header.owner;
    var sectionGroupNodes = []
    var sections = this.state.resume.sections || [];
    var previousTitle = '';
    var nextTitle = '';
    var groupSections = [];
    sections.forEach(function (section, index) {
      nextTitle = (sections.length-1 !== index) ? sections[index + 1].title : '';
      groupSections.push(section);
      if (nextTitle !== section.title) {
        sectionGroupNodes.push(this.renderGroup(groupSections));
        groupSections = [];
      }
    }.bind(this));

    return sectionGroupNodes;
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