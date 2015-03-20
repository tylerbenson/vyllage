var React = require('react');
var actions = require('../actions');

var OrganizationEdit = React.createClass({
  getInitialState: function() {
    return { organization:this.props.section }; 
  },
  handleChange: function(key, e) {
    e.preventDefault();
    var organization = this.state.organization;
    organization[key] = e.target.value;
    this.setState({organization: organization});
  },
  saveHandler: function(e) {
    e.preventDefault();
    var organization = this.state.organization;
    organization.uiEditMode = false;
    actions.putSection(organization);
  },
  cancelHandler: function(e) {
    e.preventDefault();
    this.setState({organization:this.props.section});
    actions.disableEditMode(this.props.section.sectionId);
  },
  render: function () {
    var organization = this.state.organization;
    return (
      <div>
        <div className='row'>
          <h4 className='u-pull-left'>
            <input
              className='u-full-width'
              placeholder='Organization Name'
              value={organization.organizationName}
              onChange={this.handleChange.bind(this, 'organizationName')}
            /> 
          </h4>
          <a className='button u-pull-right' onClick={this.cancelHandler}>Cancel</a>
          <a className='button u-pull-right' onClick={this.saveHandler}>Save</a>
        </div>
        <textarea 
          className='u-full-width'
          placeholder="Organization Description"
          value={organization.organizationDescription}
          onChange={this.handleChange.bind(this, 'organizationDescription')}>
        </textarea>
        <input
          className='u-full-width'
          placeholder='enter role'
          value={organization.role}
          onChange={this.handleChange.bind(this, 'role')}
        /> 
        <div className='row'>
          <input
            className='four columns'
            placeholder='start date'
            value={organization.startDate}
            onChange={this.handleChange.bind(this, 'startDate')}
          /> 
          <input
            className='four columns'
            placeholder='end date'
            value={organization.endDate}
            onChange={this.handleChange.bind(this, 'endDate')}
          />
          <input
            className='four columns'
            placeholder='location'
            value={organization.location}
            onChange={this.handleChange.bind(this, 'location')}
          />
        </div>
        <textarea
          className='u-full-width'
          placeholder='highlights'
          value={organization.highlights}
          onChange={this.handleChange.bind(this, 'highlights')}
        ></textarea>
      </div>
    );
  }
});

module.exports = OrganizationEdit;