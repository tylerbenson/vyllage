var React = require('react');
var actions = require('../actions');
var Comments = require('../comments');
var CommentsCount = require('../../buttons/comments-count');
var moment = require('moment');

var SectionFooter = React.createClass({
  clickComments: function () {
    actions.toggleComments(this.props.section.sectionId);
  },
  render: function () {
    var numberOfComments = this.props.section && this.props.section.numberOfComments;
    return (
      <div className='footer'>
        <div className='content'>
          <p className='timestamp'>{moment(this.props.section.lastModified).fromNow()}</p>
          <div className='actions'>
            <CommentsCount count={numberOfComments} onClick={this.clickComments}/>
          </div> 
        </div>
        <Comments section={this.props.section} />
      </div>
    );
  }
});

module.exports = SectionFooter;