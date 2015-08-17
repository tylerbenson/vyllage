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
var jQuery = require('jquery');

require('jquery-ui/sortable');
require('../jquery.ui.touch-punch.js');




var SubSection = React.createClass({

    componentDidMount: function () {
        jQuery('.subsection-holder').sortable({
            axis : 'y',
            cursor: "move",
            items: "div.sub-section",
            handle:'.move-sub'
        });
    },

    render: function(){
      return(
        <div className="sub-section">
          <span className="move-sub">MOVE-Sub</span>
          <Section
            key={this.props.data.sectionId}
            section={this.props.data}
            owner={this.props.owner}
           />
         </div>
      );
    }
});

var SectionGroup = React.createClass({  
    componentDidMount: function () {
        jQuery('.section-holder').sortable({
            axis : 'y',
            cursor: "move",
            items: "div.section",
            handle: '.move-section'
        });
    },
    render: function(){

      var self = this;
      var subsection = function( sub_section , index ){
          return <SubSection data={sub_section} owner={self.props.section.owner} />
      }
      return (
        <div className="section">  
            <div className="container">
              <Header
                title={this.props.section.title}
                type={this.props.section.type}
                owner={this.props.section.owner}
              />  

              <div className="subsection-holder">
                { this.props.section.child.map(subsection) }           
              </div>

            </div>
        </div>
      )
    }
});


var SectionRender =  React.createClass({   
    render: function () {   
      var render_section = function(section , index ){
         return <SectionGroup section={section} />
      }
      return (<div className="section-holder">{this.props.sections.map(render_section)}</div>);
    }
});


var ResumeEditor = React.createClass({
  
  mixins: [Reflux.connect(resumeStore, 'resume'), Reflux.connect(settingStore)],
  
  componentWillMount: function () {
    actions.getResume();
    actions.getAllsections(); // used preEmit for this 
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


  callback : function( event, itemThatHasBeenMoved, itemsPreviousIndex, itemsNewIndex, reorderedArray ){
      
  },



  render: function () {
    var owner=this.state.resume.header.owner;

    var sections = filter(this.renderSections(), function(n) {
      return n;
    });

    var allSection;

    if( this.state.resume.all_section != undefined ){    
      if( this.state.resume.all_section.length > 0 ){
        allSection = <SectionRender sections={this.state.resume.all_section} />
      }else{
        allSection = <Empty />
      }
    }
    
    return (
      <div>
        <Banner header={this.state.resume.header} settings={this.state.settings} /> 
        {allSection}  
      </div>
    );
  }
});






module.exports = ResumeEditor;