var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');
var settingStore = require('../settings/store');
var filter = require('lodash.filter');
var Header = require('./sections/Header');
var Section = require('./sections');
var Banner = require('./banner');
var sortby = require('lodash.sortby');
var EmptySections = require('./sections/Empty');
var Reorderable = require('./../reorderable');

  

  
  var ListItem =  React.createClass({   
      render: function () {
      
        return (
          <div> 
            <span className="moveme">MOVE</span>
              {this.props.item.title}

          </div>
        )

      }
  });
  




var ResumeEditor = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume'), Reflux.connect(settingStore)],
  componentWillMount: function () {
    actions.getResume();
  },
  moveSection: function (id, afterId) {
    actions.moveSection(id, afterId);
  },
  isSupportedSection: function (type) {
    //Can plugin Togglz request here
    var supported = ['SummarySection','JobExperienceSection','EducationSection','SkillsSection','CareerInterestsSection'];
    return supported.indexOf(type) > -1;
  },
  renderGroup: function (sections, groupPosition) {
    var owner=this.state.resume.header.owner;
    var containsUnsupportedSection = false;
    var subsectionNodes = sections.map(function(section) {
      if(!this.isSupportedSection(section.type)) {
        containsUnsupportedSection = true;
      }
      return <Section
          key={section.sectionId}
          section={section}
          moveSection={this.moveSection}
          owner={owner} />
    }.bind(this));

    if(containsUnsupportedSection) {
      return null;
    }

    return (
      <div key={Math.random()} className='section'>
        <div className='container'>
          <Header
            title={sections[0].title}
            type={sections[0].type}
            owner={owner}
            groupPosition={groupPosition}
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
    var groupPosition = 1;

    sections.forEach(function (section, index) {
      nextTitle = (sections.length-1 !== index) ? sections[index + 1].title : '';
      groupSections.push(section);
      if (nextTitle !== section.title) {
        sectionGroupNodes.push(this.renderGroup(groupSections, groupPosition));
        groupPosition += groupSections.length;
        groupSections = [];
      }
    }.bind(this));

    return sectionGroupNodes;
  },




  render: function () {
    var owner=this.state.resume.header.owner;
  
   

    return (
      <div>
        <Banner header={this.state.resume.header} settings={this.state.settings} />

        <Reorderable
         
          itemKey='sectionId'
         
          lock='horizontal'
         
          holdTime='0'

          handle="moveme"
         
          list={this.state.resume.sections}
         
          template={ListItem}
         
          callback={this.callback}
        
          listClass='my-list'
        
          itemClass='list-item'
         
          selected={this.state.selected}
        
          selectedKey='sectionId'
       
          disableReorder={false}/>

              

        <div className="sections">
          {this.renderSections()}
          {owner ? <EmptySections sections={this.state.resume.sections} owner={owner} />: null}
        </div>
      </div>
    );
  }
});

module.exports = ResumeEditor;