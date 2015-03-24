var React = require('react');
var actions = require('../actions');
var Comments = require('../comments');

var SectionFooter = React.createClass({
  clickComments: function () {
    actions.toggleComments(this.props.section.sectionId);
  },
  render: function () {
    var commentsCount = null; // this.props.section && this.props.section.comments && this.props.section.comments.length;
    return (
      <div>
        <div className='row resume-section-footer'>
          <p className='u-pull-left'>Just updated now</p>
          <a className='u-pull-right' onClick={this.clickComments}>
            <i className='icon ion-chatbox comment-icon'></i>
            <span className='comment-count'>{commentsCount}</span>
            comments
          </a> 
        </div>
        <Comments section={this.props.section} />
      </div>
    );
  }
});

module.exports = SectionFooter;