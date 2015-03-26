var React = require('react');
var DeleteSection = require('../Delete');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');

var OrganizationPreview = React.createClass({
  editHandler: function (e) {
    actions.enableEditMode(this.props.section.sectionId);
  },
  render: function () {
    var organization = this.props.section;
    return (
      <div>
        <div className='row'>
         <div className="twelve columns section-title">
            <p className='u-pull-left'>{organization.organizationName}</p>
            <DeleteSection className='u-pull-right' sectionId={this.props.section.sectionId} />
            <p className='u-pull-right'><EditBtn editHandler={this.editHandler}/></p>
          </div>
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