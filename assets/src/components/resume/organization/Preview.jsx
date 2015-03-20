var React = require('react');
var actions = require('../actions');

var OrganizationPreview = React.createClass({
  editHandler: function (e) {
    e.preventDefault();
    actions.enableEditMode(this.props.section.sectionId);
  },
  render: function () {
    var organization = this.props.section;
    return (
      <div>
        <div className='row'>
          <h4 className='u-pull-left'>{organization.title}</h4>
          <a className='button u-pull-right' onClick={this.editHandler}>Edit</a>
        </div>
        <p>{organization.description}</p>
        <p>{organization.role}</p>
        <p>{organization.startDate}</p>
        <p>{organization.endDate}</p>
        <p>{organization.location}</p>
        <p>{organization.roleDescription}</p>
        <p>{organization.highlights}</p>
      </div>
    );
  }
});

module.exports = OrganizationPreview;