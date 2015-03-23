var React = require('react');

var SectionFooter = React.createClass({
  render: function () {
    return (
      <div className='row resume-section-footer'>
        <p className='u-pull-left update-time'>Updated just now</p>
        <a className='u-pull-right'>comments</a> 
        <a className='u-pull-right'>edits</a> 
      </div>
    );
  }
});

module.exports = SectionFooter;