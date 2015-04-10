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
  endDateCheckbox: function (e) {
    var section = this.state.section;
    section.endDate = (section.endDate === 'Present')? '': 'Present';
    this.setState({section: section});
  },
  render: function () {
    var section = this.state.section;
    var placeholders = this.props.placeholders || {};
    return (
      <div className ="subsection">
        <div className='header'>
          <div className='title'>
            <h2>
              <input
                ref='organizationName'
                disabled={!this.state.uiEditMode}
                className='flat'
                placeholder='Organization Name'
                type='text'
                value={section.organizationName}
                onChange={this.handleChange.bind(this, 'organizationName')}
             />
            </h2>
          </div>
          <div className="actions">
            {this.state.uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/>}
            {this.state.uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: <DeleteSection sectionId={this.props.section.sectionId} />}
          </div>
        </div>
        <div className='content'>
          <Textarea
            disabled={!this.state.uiEditMode}
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
                    disabled={!this.state.uiEditMode}
                    className="flat"
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
                disabled={!this.state.uiEditMode}
                className="flat"
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
                  disabled={!this.state.uiEditMode}
                  type="text"
                  className="inline flat date"
                  placeholder="Start Date"
                />
              </Datepicker>
              -
              <Datepicker
                name='endDate'
                date={section.endDate}
                setDate={this.handleChange}
              >
                <input
                  disabled={!this.state.uiEditMode}
                  type="text"
                  className="inline flat date"
                  placeholder="End Date"
                />
              </Datepicker>
              {this.state.uiEditMode? <span><input
                disabled={!this.state.uiEditMode}
                type="checkbox"
                className="inline flat"
                name='endDate'
                checked={section.endDate === 'Present'}
                onChange={this.endDateCheckbox}
              /> Currently I am here</span>: null}
              <input
                disabled={!this.state.uiEditMode}
                type="text"
                className="flat location"
                placeholder="Location"
                value={section.location}
                onChange={this.handleChange.bind(this, 'location')}
              />
              <Textarea
                disabled={!this.state.uiEditMode}
                className="flat"
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