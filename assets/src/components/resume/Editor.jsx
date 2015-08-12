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
var Empty = require('./sections/Empty');
var Reorderable = require('./../reorderable');

  
  
var ListItem =  React.createClass({   
    render: function () {      
      return (
        <div> 
          <span className="move-section">MOVE</span> 
            <strong>{this.props.item.title}</strong>
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
  renderGroup: function (sections, groupPosition) {
    var owner=this.state.resume.header.owner;
    var subsectionNodes = sections.map(function(section) {
      return <Section
          key={section.sectionId}
          section={section}
          moveSection={this.moveSection}
          owner={owner} />
    }.bind(this));

    return (
      <div className='section'>
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
    var sections = filter(this.state.resume.sections, {isSupported: true}) || [];
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

    var sections = filter(this.renderSections(), function(n) {
      return n;
    });



    window.someting = this.state;

    return (
      <div>
        <Banner header={this.state.resume.header} settings={this.state.settings} />

        <Reorderable         
          itemKey='type'         
          lock='horizontal'         
          holdTime='0'
          handle="move-section"         
          list={this.state.resume.all_section}         
          template={ListItem}         
          callback={this.callback}        
          listClass='my-list'        
          itemClass='list-item'         
          selected={this.state.selected}        
          selectedKey='type'       
          disableReorder={false}/>

              

        <div className="sections">
          { sections.length > 0 ? sections : <Empty /> }
        </div>
      </div>
    );
  }
});

module.exports = ResumeEditor;