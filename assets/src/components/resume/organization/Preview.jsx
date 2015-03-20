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
          <h4 className='u-pull-left'>{organization.organizationName}</h4>
          <a className='button u-pull-right' onClick={this.editHandler}>Edit</a>
        </div>
        <p>{organization.organizationDescription}</p>
        <p>{organization.role}</p>
        <div className='row'>
          <p>{organization.startDate}</p>
          <p>{organization.endDate}</p>
          <p>{organization.location}</p>
        </div>
        <p>{organization.roleDescription}</p>
        <p>{organization.highlights}</p>
      </div>
    );
  }
});

module.exports = OrganizationPreview;