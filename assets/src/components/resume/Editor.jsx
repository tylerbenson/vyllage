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
            placeholder: "ui-sortable-placeholder",
            opacity: 0.85,
            scrollSpeed: 15,
            scrollSensitivity: 20,
            forcePlaceholderSize: true,
            cursor: "move",
            items: "div.subsection-wrapper",
            handle:'.move-sub',
            start: function(event, ui) {
              jQuery('.banner .subheader').hide();
            },
            stop : function(event, ui){
              jQuery('.banner .subheader').show();
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
            placeholder: "ui-sortable-placeholder",
            opacity: 0.85,
            scrollSpeed: 15,
            scrollSensitivity: 20,
            forcePlaceholderSize: true,
            cursor: "move",
            items: "div.section",
            handle: ".move-section",
            start: function(event, ui) {
              jQuery('.banner .subheader').hide();
            },
            stop : function(event, ui){
              jQuery('.banner .subheader').show();
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
    actions.getAllsections(); //preEmit
  },
  render: function () {
    var owner = this.state.resume.header.owner;
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