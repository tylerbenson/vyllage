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

  

var SectionItem = React.createClass({
    mixins: [ Reflux.connect(resumeStore, 'resume')],

    render: function(){
      return (
        <div>
         
         <span className="move-section-sub">move sub</span> 


        <Section
          key={this.props.item.sectionId}
          section={this.props.item}
          owner={this.props.sharedProps}
         />


           
        </div>
      )
    }
});



var SubSection = React.createClass({
    
    getInitialState : function(){
      return {
        elements: this.props.data
      }
    },

    callback:function( event, itemThatHasBeenMoved, itemsPreviousIndex, itemsNewIndex, reorderedArray){
        console.log(reorderedArray);
    },



    render: function(){
      
      return (
          <Reorderable         
            itemKey='sectionId'         
            lock='horizontal'         
            holdTime='0'
            handle="move-section-sub"         
            list={this.state.elements }         
            template={SectionItem}         
            callback={this.callback}        
            listClass='subsections'        
            itemClass='subsection-holder'         
            selected={this.state.selected}        
            selectedKey='sectionId'       
            disableReorder={false}
            sharedProps={this.props.owner} />
      )
    }
});



  
var SectionGroup =  React.createClass({   
    render: function () {   

     

      return (
        <div className="container"> 

        <span className="move-section">MOVE</span> 
  
          <Header
            title={this.props.item.title}
            type={this.props.item.type}
            owner={this.props.item.owner}
          />      
          <SubSection owner={this.props.item.owner} data={this.props.item.child} />

        </div>
      )
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
        allSection = <Reorderable         
              itemKey='id'         
              lock='horizontal'         
              holdTime='0'
              handle="move-section"         
              list={this.state.resume.all_section}         
              template={SectionGroup}         
              callback={this.callback}        
              listClass='section-holder'        
              itemClass='section'         
              selected={this.state.selected}        
              selectedKey='id'       
              disableReorder={false}/>


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