var React = require('react');
var actions = require('../actions');
var Comments = require('../comments');
var CommentsCount = require('../../buttons/comments-count');


var SectionFooter = React.createClass({
  clickComments: function () {
    actions.toggleComments(this.props.section.sectionId);
  },
  render: function () {
    var numberOfComments = this.props.section && this.props.section.numberOfComments;
    return (
      <div>
         <div className='row resume-section-footer'>
          <p className='u-pull-left update-time'>Updated just now</p>
          <span className='u-pull-right' onClick={this.clickComments}>
            <CommentsCount count={numberOfComments} />
          </span> 
        </div>
        <Comments section={this.props.section} />
      </div>
    );
  }
});

module.exports = SectionFooter;