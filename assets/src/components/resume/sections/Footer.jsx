var React = require('react');
var actions = require('../actions');
var Comments = require('../comments');
var Advices = require('../advices');
var CommentsCount = require('../../buttons/comments-count');
var AdvicesCount = require('../../buttons/advices-count');
var moment = require('moment');

var SectionFooter = React.createClass({
  getInitialState: function () {
      return {
          'section': {}  
      };
  },
  stopPropagation: function (e) {
    e.preventDefault();
    e.stopPropagation();
  },
  componentWillReceiveProps: function (nextProps) {
      var section = nextProps.section;
      section.showEdits = false;
      this.setState({ section : section });  
  },

  clickComments: function (e) {
    actions.toggleComments(this.props.section.sectionId);
  },
  clickEdits: function(e){
    var section = this.state.section;
    section.showEdits = !section.showEdits;
    this.setState({section:section});
  },
  hideComments: function () {
    actions.hideComments(this.props.section.sectionId);
  },
  render: function () {
    var numberOfComments = this.props.section && this.props.section.numberOfComments;
    if( numberOfComments == undefined ){
      numberOfComments = 0;
    }
    var numberOfAdvices = 0;
    if( this.state.section.advices != undefined ){
      this.state.section.advices.map(function(advice , index){
        if(advice.status == 'pending' || advice.status == null ) numberOfAdvices++;
      });
    }
    var lastModified = this.props.section.lastModified;

    
    return (
      <div className='footer' onClick={this.stopPropagation}>
        <div className='content'>
          <p className='timestamp'>
            {moment(lastModified).isValid() ? moment.utc(lastModified).fromNow(): ''}
          </p>
          <div className='actions'>
           { numberOfAdvices > 0 ? <AdvicesCount count={numberOfAdvices} onClick={this.clickEdits} /> : null }
            <CommentsCount count={numberOfComments} onClick={this.clickComments} showComments={this.props.section.showComments} />
          </div> 
        </div>
        <Comments section={this.props.section} owner={this.props.owner} />
        <Advices section={this.state.section} owner={this.props.owner} />
      </div>
    );
  }
});

module.exports = SectionFooter;