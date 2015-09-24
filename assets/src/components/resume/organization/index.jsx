var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');
var DeleteSection = require('../Delete');
var SaveBtn = require('../../buttons/save');
var SuggestionBtn = require('../../buttons/suggestion');
var CancelBtn = require('../../buttons/cancel');
var Textarea = require('react-textarea-autosize');
var Datepicker = require('../../datepicker');
var Highlights = require('../highlights');
var assign = require('lodash.assign')
var MoveButton = require('../../buttons/move');
var SectionFooter = require('../sections/Footer');
var ConfirmUnload = require('../ConfirmUnload');
var cx = require('react/lib/cx');
var cloneDeep = require('clone-deep');

var Organization = React.createClass({
  getInitialState: function () {
    return {
      section: this.props.section,
      uiEditMode: this.props.section.newSection,
      newSection: this.props.section.newSection
    };
  },

  // componentWillReceiveProps: function (nextProps) {
  //   this.setState({
  //     section: nextProps.section
  //   });
  // },
  componentDidMount: function() {
    if (this.state.uiEditMode) {
      this.refs.organizationName.getDOMNode().focus();
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
    section['highlights'] = this.refs.highlights.getHighlights();
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
      this.refs.organizationName.getDOMNode().focus();
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
                  ref='organizationName'
                  disabled={!uiEditMode}
                  className='flat'
                  style={uiEditMode || section.organizationName ? {}: {display: 'none'}}
                  placeholder='Organization Name'
                  type='text'
                  value={section.organizationName}
                  rows="1"
                  onChange={this.handleChange.bind(this, 'organizationName')}
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
              style={uiEditMode || section.organizationDescription ? {}: {display: 'none'}}
              className="flat"
              rows="1"
              autoComplete="off"
              placeholder="Organization Description"
              value={section.organizationDescription}
              onChange={this.handleChange.bind(this, 'organizationDescription')}
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
                      placeholder={placeholders.role || "Degree / Position"}
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
                <Highlights ref="highlights" highlights={section.highlights} uiEditMode={uiEditMode} />
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
    section['highlights'] = this.refs.highlights.getHighlights();
    actions.saveSectionAdvice(section);
    
    this.setState({
      section : this.props.section,
      uiEditMode: false
    });
  }
});

module.exports = Organization;