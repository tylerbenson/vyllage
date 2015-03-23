var React = require('react');
var actions = require('../actions');

var FreeformPreview = React.createClass({
  editHandler: function (e) {
    e.preventDefault();
    actions.enableEditMode(this.props.section.sectionId);
  },
  render: function () {
    var className = this.props.className + '-preview';
    return (
      <div className={className}>
        <div className='row'>
          <h4 className='u-pull-left resume-section-title'>{this.props.title}</h4>
          <a className='button u-pull-right' onClick={this.editHandler}>Edit</a>
        </div>
        <p>
          {this.props.section.description}
        </p>
      </div>
    );
  }
});

module.exports = FreeformPreview;