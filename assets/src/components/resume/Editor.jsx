var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');
var settingStore = require('../settings/store');
var filter = require('lodash.filter');
var Header = require('./sections/Header');
var Section = require('./sections');
var Banner = require('./banner');
var Empty = require('./sections/Empty');
var Loading = require('./sections/Loading');
var Sortable = require('../util/Sortable');
var Tour = require('../tour');
var isSorting = false;

var SubSection = React.createClass({
  start: function(event, ui){
    jQuery('.banner .subheader')
      .removeClass('visible')
      .addClass('dragging');
  },
  stop: function(event, ui){
    var subsection_order = [];
    var type;

    jQuery(event.target).children().each(function(index) {
      subsection_order.push({
        sectionId : jQuery(this).data("id"),
        index : index
      });

      type = jQuery(this).attr("rel");
    });

    actions.moveSectionOrder(subsection_order , type);
    jQuery('.banner .subheader').removeClass('dragging');
    jQuery(ui.item).css('z-index','');
  },
  render: function(){
    var config = {
      list: ".subsection-holder",
      items: "div.subsection-wrapper",
      handle: ".move-sub",
      axis : 'y',
      start: this.start,
      stop: this.stop
    };
    return(
      <Sortable config={config} className="subsection-wrapper" rel={this.props.data.type} data-id={this.props.data.sectionId}>
        <Section
          key={this.props.data.sectionId}
          section={this.props.data}
          owner={this.props.owner}
          isSorting={isSorting}
         />
       </Sortable>
    );
  }
});

var SectionGroup = React.createClass({
  start: function(event, ui) {
    jQuery('.banner .subheader')
      .removeClass('visible')
      .addClass('dragging');
  },
  stop: function(event, ui){
    var order = [];
    jQuery(event.target).children().each(function(index) {
        order.push({
          type : jQuery(this).attr("rel"),
          index : index
        });
    });
    actions.moveGroupOrder(order);
    jQuery('.banner .subheader').removeClass('dragging');
    jQuery(ui.item).css('z-index','');
  },
  render: function(){
    var self = this;
    var subsection = function( sub_section , index ){
        return <SubSection key={index} data={sub_section} owner={self.props.section.owner} />
    };
    var config = {
      list: ".section-holder",
      items: "div.section",
      handle: ".move-section",
      axis : 'y',
      start: this.start,
      stop: this.stop
    };
    return (
      <Sortable config={config} className="section" rel={this.props.section.type}>
        <div className="container">
          { this.props.section.owner && isSorting ?
            <span className="inverted secondary button small move move-section" {...this.props}>
              <i className="ion-arrow-move"></i>
              Move
            </span> : null
          }
          <Header
            title={this.props.section.title}
            type={this.props.section.type}
            owner={this.props.section.owner}
          />

          <div className="subsection-holder">
            { this.props.section.child.map(subsection) }
          </div>
        </div>
      </Sortable>
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
    actions.getAllSections(); //preEmit
  },
  render: function () {
    var owner = this.state.resume.header.owner;
    var allSections = this.state.resume.all_section;
    var sections = filter(this.state.resume.sections, {isSupported: true});
    var content;
    isSorting = this.state.resume.isSorting;

    if(allSections !== undefined) {
      if(allSections.length > 0) {
        content = <SectionRender sections={allSections} />;
      }
      else if(owner === undefined || sections.length > 0) {
        content = <Loading />;
      }
      else if(sections.length === 0 && allSections.length === 0) {
        content = <Empty />;
      }
    }
    else {
      content = <Empty />
    }

    return (
      <div>
        <Tour page="resume" />
        <Banner header={this.state.resume.header} ownDocumentId={this.state.resume.ownDocumentId} settings={this.state.settings} sections={sections} />
        {content}
      </div>
    );
  }
});


module.exports = ResumeEditor;