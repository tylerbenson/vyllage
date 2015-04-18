var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');
var DeleteSection = require('../Delete');
var SaveBtn = require('../../buttons/save');
var CancelBtn = require('../../buttons/cancel');
var Textarea = require('react-textarea-autosize');
var Datepicker = require('../../datepicker');
var assign = require('lodash.assign')

var Organization = React.createClass({
  getInitialState: function () {
    return {
      section: assign({}, this.props.section),
      uiEditMode: this.props.section.newSection,
      newSection: this.props.section.newSection
    };
  },
  componentWillReceiveProps: function (nextProps) {
    this.setState({
      section: nextProps.section,
    });
  },
  componentDidMount: function() {
    this.refs.organizationName.getDOMNode().focus();
  },
  handleChange: function(key, e) {
    // e.preventDefault();
    var section = this.state.section;
    section[key] = e.target.value;
    this.setState({section: section});
  },
  toggleCurrent: function () {
    var section = this.state.section;
    section.isCurrent = !section.isCurrent;
    this.setState({section: section});
  },
  saveHandler: function(e) {
    var section = this.state.section;
    actions.putSection(section);
    this.setState({
      uiEditMode: false
    });
  },
  cancelHandler: function(e) {
    var section = this.props.section;
    if (section.newSection) {
      actions.deleteSection(section.sectionId);
    } else {
      this.setState({
        section: this.props.section,
        uiEditMode: false
      });
    }
  },
  editHandler: function (e) {
    this.setState({
      uiEditMode: true
    }, function () {
      this.refs.organizationName.getDOMNode().focus();
    });
  },
  render: function () {
    var section = this.state.section;
    var uiEditMode = this.state.uiEditMode;
    var placeholders = this.props.placeholders || {};
    return (
      <div className ="subsection">
        <div className='header'>
          <div className='title'>
            <h2>
              <input
                ref='organizationName'
                disabled={!uiEditMode}
                className='flat'
                style={uiEditMode || section.organizationName ? {}: {display: 'none'}}
                placeholder='Organization Name'
                type='text'
                value={section.organizationName}
                onChange={this.handleChange.bind(this, 'organizationName')}
             />
            </h2>
          </div>
          {this.props.owner? <div className="actions">
            {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/>}
            {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: <DeleteSection sectionId={this.props.section.sectionId} />}
          </div>: null}
        </div>
        <div className='content'>
          <Textarea
            disabled={!uiEditMode}
            style={uiEditMode || section.organizationDescription ? {}: {display: 'none'}}
            className="flat"
            rows="1"
            autoComplete="off"
            placeholder="Organization Description"
            value={section.organizationDescription}
            onChange={this.handleChange.bind(this, 'organizationDescription')}
          ></Textarea>
          <section className="subsubsection">
            <div className="header">
              <div className="title">
                <h3>
                  <input
                    disabled={!uiEditMode}
                    className="flat"
                    style={uiEditMode || section.role ? {}: {display: 'none'}}
                    type="text"
                    placeholder={placeholders.role || "Degree / Position"}
                    value={section.role}
                    onChange={this.handleChange.bind(this, 'role')}
                  />
                </h3>
              </div>
            </div>
            <div className="content">
              <Textarea
                disabled={!uiEditMode}
                className="flat"
                style={uiEditMode || section.roleDescription ? {}: {display: 'none'}}
                rows="1"
                placeholder="Role Description"
                value={section.roleDescription}
                onChange={this.handleChange.bind(this, 'roleDescription')}
              ></Textarea>
              <Datepicker
                name='startDate'
                date={section.startDate}
                setDate={this.handleChange}
              >
                <input
                  disabled={!uiEditMode}
                  style={uiEditMode || section.startDate ? {}: {display: 'none'}}
                  type="text"
                  className="inline flat date"
                  placeholder="Start Date"
                />
              </Datepicker>
              {(uiEditMode || (section.startDate && (section.endDate || section.isCurrent)))? '-': null}
              <Datepicker
                name='endDate'
                date={section.isCurrent? "Present": section.endDate}
                setDate={this.handleChange}
                isCurrent={section.isCurrent}
                toggleCurrent={this.toggleCurrent}
              >
                <input
                  disabled={!uiEditMode}
                  style={uiEditMode || section.endDate || section.isCurrent ? {}: {display: 'none'}}
                  type="text"
                  className="inline flat date"
                  placeholder="End Date"
                />
              </Datepicker>
              <input
                disabled={!uiEditMode}
                type="text"
                className="flat location"
                style={uiEditMode || section.location ? {}: {display: 'none'}}
                placeholder="Location"
                value={section.location}
                onChange={this.handleChange.bind(this, 'location')}
              />
              <Textarea
                disabled={!uiEditMode}
                className="flat"
                style={uiEditMode || section.highlights ? {}: {display: 'none'}}
                rows="1"
                placeholder="Note at least three (3) notable accomplishments achieved during this position.."
                value={section.highlights}
                onChange={this.handleChange.bind(this, 'highlights')}
              ></Textarea>
            </div>
          </section>
        </div>
      </div>
    );
  }
});

module.exports = Organization;