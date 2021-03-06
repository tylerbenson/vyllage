var React = require('react');
var actions = require('../actions');
var Comments = require('../comments');
var Advices = require('../advices');
var CommentsCount = require('../../buttons/comments-count');
var AdvicesCount = require('../../buttons/advices-count');
var moment = require('moment');
var FeatureToggle = require('../../util/FeatureToggle');

var SectionFooter = React.createClass({
  stopPropagation: function (e) {
    e.preventDefault();
    e.stopPropagation();
  },
  clickComments: function (e) {
    actions.toggleComments(this.props.section.sectionId);
  },
  clickEdits: function(e){
    actions.toggleEdits( this.props.section.sectionId );
   // this.setState({showEdits:!this.state.showEdits});
  },
  hideComments: function () {
    actions.hideComments(this.props.section.sectionId);
  },
  render: function () {
    var numberOfComments = this.props.section && this.props.section.numberOfComments;
    if( numberOfComments == undefined ){
      numberOfComments = 0;
    }
    var lastModified = this.props.section.lastModified;

    return (
      <div className='footer' onClick={this.stopPropagation}>
        <div className='content'>
          <p className='timestamp'>
            {moment(lastModified).isValid() ? moment.utc(lastModified).fromNow(): ''}
          </p>
          <div className='actions'>
           <FeatureToggle name="SECTION_ADVICE">
             { this.props.section.numberOfSuggestedEdits  ? <AdvicesCount count={this.props.section.numberOfSuggestedEdits} onClick={this.clickEdits} showEdits={this.props.section.showEdits} /> : null }
           </FeatureToggle>
            <CommentsCount count={numberOfComments} onClick={this.clickComments} showComments={this.props.section.showComments} />
          </div>
        </div>
        <Comments section={this.props.section} owner={this.props.owner} />
        <FeatureToggle name="SECTION_ADVICE">{ this.props.section != undefined ? <Advices section={this.props.section} owner={this.props.owner} /> : null }</FeatureToggle>
      </div>
    );
  }
});

module.exports = SectionFooter;