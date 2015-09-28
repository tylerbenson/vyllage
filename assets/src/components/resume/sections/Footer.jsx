var React = require('react');
var actions = require('../actions');
var Comments = require('../comments');
var Advices = require('../advices');
var CommentsCount = require('../../buttons/comments-count');
var AdvicesCount = require('../../buttons/advices-count');
var moment = require('moment');

var SectionFooter = React.createClass({
  stopPropagation: function (e) {
    e.preventDefault();
    e.stopPropagation();
  },
  clickComments: function (e) {
    actions.toggleComments(this.props.section.sectionId);
  },
  clickEdits: function(e){
    this.setState({showEdits:!this.state.showEdits});
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
           { this.props.section.numberOfAdvices  ? <AdvicesCount count={this.props.section.numberOfAdvices} onClick={this.clickEdits} /> : null }
            <CommentsCount count={numberOfComments} onClick={this.clickComments} showComments={this.props.section.showComments} />
          </div>
        </div>
        <Comments section={this.props.section} owner={this.props.owner} />
        { this.props.section != undefined ? <Advices section={this.props.section} owner={this.props.owner} /> : null }
      </div>
    );
  }
});

module.exports = SectionFooter;