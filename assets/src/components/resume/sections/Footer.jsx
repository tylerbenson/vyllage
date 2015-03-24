var React = require('react');
var EditsCount = require('../../buttons/edits-count');
var CommentsCount = require('../../buttons/comments-count');

var SectionFooter = React.createClass({
  render: function () {
    return (
      <div className='row resume-section-footer'>
        <p className='u-pull-left update-time'>Updated just now</p>
        <span className='u-pull-right'><CommentsCount /></span> 
        <span className='u-pull-right'><EditsCount /></span> 
      </div>
    );
  }
});

module.exports = SectionFooter;