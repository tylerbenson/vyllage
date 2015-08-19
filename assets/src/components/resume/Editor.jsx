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

window.jQuery = require('jquery');
require('jquery-ui/sortable');
require('jquery-ui-touch-punch');

var SubSection = React.createClass({

    componentDidMount: function () {

        var self = this;

        jQuery('.subsection-holder').sortable({
            axis : 'y',
            cursor: "move",
            items: "div.subsection-wrapper",
            handle:'.move-sub',
            stop : function( event, ui ){

              var subsection_order = [];
              var type;
              jQuery(this).children().each(function(index) {
                  subsection_order.push({
                    sectionId : jQuery(this).data('id'),
                    index : index
                  });

                  type = jQuery(this).attr('rel');
              });
              actions.moveSectionOrder(subsection_order , type );


            }
        });
    },

    render: function(){
      return(
        <div className="subsection-wrapper" rel={this.props.data.type} data-id={this.props.data.sectionId}>
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

        var self = this;

        jQuery('.section-holder').sortable({
            axis : 'y',
            cursor: "move",
            items: "div.section",
            handle: '.move-section',
            stop: function( event, ui ) {
              var order = [];
              jQuery('.section-holder').children().each(function(index) {
                  order.push({
                    type : jQuery(this).attr('rel'),
                    index : index
                  });
              });
              actions.moveGroupOrder(order);

            }
        });
    },
    render: function(){

      var self = this;
      var subsection = function( sub_section , index ){
          return <SubSection key={index} data={sub_section} owner={self.props.section.owner} />
      }
      return (
        <div className="section" rel={this.props.section.type}>
          <div className="container">
            <span className="inverted secondary button small move move-section" {...this.props}>
              <i className="ion-arrow-move"></i>
              Move
            </span>
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
         return <SectionGroup key={index} section={section} />
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