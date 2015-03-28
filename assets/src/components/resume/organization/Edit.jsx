var React = require('react');
var actions = require('../actions');

var SaveBtn = require('../../buttons/save');
var CancelBtn = require('../../buttons/cancel');

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
    var organization = this.state.organization;
    organization.uiEditMode = false;
    actions.putSection(organization);
  },
  cancelHandler: function(e) {
    this.setState({organization:this.props.section});
    actions.disableEditMode(this.props.section.sectionId);
  },
  render: function () {
    var organization = this.state.organization;
    return (
      <div>
        <div className='header'>
          <h1>{this.props.title}</h1>
          <div className='pull right actions'>  
            <SaveBtn saveHandler={this.saveHandler}/>
            <CancelBtn cancelHandler={this.cancelHandler}/>
          </div>
        </div>
      <div className='fields'>
        <input
            className='u-full-width'
            placeholder='Organization Name'
            value={organization.organizationName}
            onChange={this.handleChange.bind(this, 'organizationName')}
          /> 
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
        <input
          className='u-full-width'
          placeholder='start date'
          value={organization.startDate}
          onChange={this.handleChange.bind(this, 'startDate')}
        /> 
        <input
          className='u-full-width'
          placeholder='end date'
          value={organization.endDate}
          onChange={this.handleChange.bind(this, 'endDate')}
        />
        <input
          className='u-full-width'
          placeholder='location'
          value={organization.location}
          onChange={this.handleChange.bind(this, 'location')}
        />
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