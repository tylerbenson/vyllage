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
  // componentDidMount: function () {
  //   var html = document.querySelector('html');
  //   html.addEventListener('click', this.hideComments);
  // },
  // componentWillUnmount: function () {
  //   document.body.removeEventListener('click', this.hideComments);
  // },
  render: function () {
    var numberOfComments = this.props.section && this.props.section.numberOfComments;
    return (
      <div className='footer' onClick={this.stopPropagation}>
        <div className='content'>
          <p className='timestamp'>{moment.utc(this.props.section.lastModified).fromNow()}</p>
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