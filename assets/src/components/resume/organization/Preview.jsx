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
        <div className='header'>
         <div className="twelve columns section-title">
            <h1>{organization.organizationName}</h1>
            <div className='pull right actions'>
              <EditBtn editHandler={this.editHandler}/>
              <DeleteSection sectionId={this.props.section.sectionId} />
            </div>
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