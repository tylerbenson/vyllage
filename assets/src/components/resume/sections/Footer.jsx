var React = require('react');
var actions = require('../actions');
var Comments = require('../comments');
var EditsCount = require('../../buttons/edits-count');
var CommentsCount = require('../../buttons/comments-count');


var SectionFooter = React.createClass({
  clickComments: function () {
    actions.toggleComments(this.props.section.sectionId);
  },
  render: function () {
    var commentsCount = null; // this.props.section && this.props.section.comments && this.props.section.comments.length;
    return (
      <div>
         <div className='row resume-section-footer'>
          <p className='u-pull-left update-time'>Updated just now</p>
          <span className='u-pull-right' onClick={this.clickComments}><CommentsCount /></span> 
        </div>
        <Comments section={this.props.section} />
      </div>
    );
  }
});

module.exports = SectionFooter;