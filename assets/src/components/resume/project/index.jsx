var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');
var DeleteSection = require('../Delete');
var SaveBtn = require('../../buttons/save');
var SuggestionBtn = require('../../buttons/suggestion');
var CancelBtn = require('../../buttons/cancel');
var Textarea = require('react-textarea-autosize');
var Datepicker = require('../../datepicker');
var assign = require('lodash.assign')
var MoveButton = require('../../buttons/move');
var SectionFooter = require('../sections/Footer');
var ConfirmUnload = require('../ConfirmUnload');
var cx = require('react/lib/cx');
var cloneDeep = require('clone-deep');

var Project = React.createClass({
  getInitialState: function () {
    return {
      section: this.props.section,
      uiEditMode: this.props.section.newSection,
      newSection: this.props.section.newSection
    };
  },
  componentDidMount: function() {
    if (this.state.uiEditMode) {
      this.refs.projectTitle.getDOMNode().focus();
    }
  },
  handleChange: function(key, e) {
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
      section: section,
      uiEditMode: false
    });
  },
  cancelHandler: function(e) {
    var section = this.props.section;
    if (section.newSection) {
      actions.deleteSection(section.sectionId);
    } else {
      this.setState({
        section: section,
        uiEditMode: false
      });
    }
  },
  editHandler: function (e) {
    this.setState({
      uiEditMode: true
    }, function () {
      this.refs.projectTitle.getDOMNode().focus();
    });
  },
  render: function () {
    var section = this.state.section;
    var uiEditMode = this.state.uiEditMode;
    var placeholders = this.props.placeholders || {};

    var classes = cx({
      'single': !this.props.isMultiple,
      'subsection': true
    });

    return (
      <div>
        <div className={classes}>
          { this.props.owner? <MoveButton />: null }
          <div className='header'>
            <div className='title'>
              <h2>
                <Textarea
                  ref='projectTitle'
                  disabled={!uiEditMode}
                  className='flat'
                  style={uiEditMode || section.projectTitle ? {}: {display: 'none'}}
                  placeholder='Project Title'
                  type='text'
                  value={section.projectTitle}
                  rows="1"
                  onChange={this.handleChange.bind(this, 'projectTitle')}
                />
              </h2>
            </div>
            {this.props.owner? <div className="actions">
              {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/>}
              {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: <DeleteSection sectionId={this.props.section.sectionId} />}
            </div>:  <div className="actions">
               {uiEditMode? <SuggestionBtn onClick={this._saveSuggestionHandler}/>: <EditBtn onClick={this.editHandler}/>}
               {uiEditMode?  <CancelBtn onClick={this.cancelHandler}/>: null }
            </div>
            }


          </div>
          <div className='content'>
            <Textarea
              disabled={!uiEditMode}
              style={uiEditMode || section.projectDescription ? {}: {display: 'none'}}
              className="flat"
              rows="1"
              autoComplete="off"
              placeholder="Project Description"
              value={section.projectDescription}
              onChange={this.handleChange.bind(this, 'projectDescription')}
            />
            <section className="subsubsection">
              <div className="header">
                <div className="title">
                  <h3>
                    <Textarea
                      disabled={!uiEditMode}
                      className="flat"
                      style={uiEditMode || section.role ? {}: {display: 'none'}}
                      type="text"
                      placeholder={placeholders.role || "Role"}
                      rows="1"
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
                  placeholder={placeholders.roleDescription || "Role Description"}
                  value={section.roleDescription}
                  onChange={this.handleChange.bind(this, 'roleDescription')}
                />
                <Datepicker
                  name='projectDate'
                  date={section.projectDate}
                  setDate={this.handleChange}
                >
                  <input
                    disabled={!uiEditMode}
                    style={uiEditMode || section.projectDate ? {}: {display: 'none'}}
                    type="text"
                    className="inline flat project date"
                    placeholder="Project Date"
                  />
                </Datepicker>
              </div>
            </section>
          </div>
          <SectionFooter section={section} owner={this.props.owner} />
        </div>
        {this.state.uiEditMode ? <ConfirmUnload onDiscardChanges={this.cancelHandler} /> : null}
      </div>
    );
  },
  _saveSuggestionHandler : function(){
    var section = cloneDeep(this.state.section);
    actions.saveSectionAdvice(section);

    this.setState({
      section : this.props.section,
      uiEditMode: false
    });
  }
});

module.exports = Project;