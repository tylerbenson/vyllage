var React = require('react');
var actions = require('../actions');
var Comments = require('../comments');
var CommentsCount = require('../../buttons/comments-count');
var moment = require('moment');

var SectionFooter = React.createClass({
  stopPropagation: function (e) {
    e.preventDefault();
    e.stopPropagation();
  },
  clickComments: function (e) {
    actions.toggleComments(this.props.section.sectionId);
  },
  hideComments: function () {
    actions.hideComments(this.props.section.sectionId);
  },
  render: function () {
    var numberOfComments = this.props.section && this.props.section.numberOfComments;
    var lastModified = this.props.section.lastModified;
    return (
      <div className='footer' onClick={this.stopPropagation}>
        <div className='content'>
          <p className='timestamp'>
            {moment(lastModified).isValid() ? moment.utc(lastModified).fromNow(): ''}
          </p>
          <div className='actions'>
            <CommentsCount count={numberOfComments} onClick={this.clickComments} showComments={this.props.section.showComments} />
          </div> 
        </div>
        <Comments section={this.props.section} owner={this.props.owner} />
      </div>
    );
  }
});

module.exports = SectionFooter;