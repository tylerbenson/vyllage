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
var classnames = require('classnames');
var cloneDeep = require('clone-deep');
var FeatureToggle = require('../../util/FeatureToggle');

var Organization = React.createClass({
  getInitialState: function () {
    return {
      section : cloneDeep(this.props.section),
      uiEditMode: this.props.section.newSection,
      newSection: this.props.section.newSection ,
      error : false
    };
  },

  // componentWillReceiveProps: function (nextProps) {
  //   if( nextProps.section != undefined )
  //   this.setState({
  //     section: cloneDeep(nextProps.section)
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
    this.setState({ section: section });
    this.validateSection( section );
  },
  toggleCurrent: function () {
    var section = this.state.section;
    section.isCurrent = !section.isCurrent;
    this.setState({section: section});
  },
  saveHandler: function(e) {
    var section = this.state.section;
    section['highlights'] = this.refs.highlights.getHighlights();
    if( this.validateSection(section) == false ){
      actions.putSection(section);
      this.setState({
        section: section,
        uiEditMode: false
      });
    }

  },
  validateSection : function( section ){
    if( section.organizationName == undefined || section.organizationName.length <= 0 ){
      this.setState({ error : true });
      return true;
    }else{
      this.setState({ error : false });
      return false;
    }
  },
  cancelHandler: function(e) {
    var section = cloneDeep(this.props.section);
    if (section.newSection) {
      actions.deleteNewSection();
    } else {
      this.setState({
        section: section,
        uiEditMode: false,
        error :false
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

    var classes = classnames({
      'single': !this.props.isMultiple,
      'subsection': true
    });

    return (
      <div>
        <div className={classes}>
          { this.props.owner && this.props.isSorting ? <MoveButton />: null }
          <div className='header'>
            <div className='title'>
              <h2>
                <Textarea
                  ref='organizationName'
                  disabled={!uiEditMode}
                  className={(this.state.error == true ? "error " : "") + "flat"}
                  style={uiEditMode || section.organizationName ? {}: {display: 'none'}}
                  placeholder='Organization Name'
                  type='text'
                  value={section.organizationName}
                  rows="1"
                  onChange={this.handleChange.bind(this, 'organizationName')}
                />
              </h2>
              { this.state.error == true ? <p className='error'><i className='ion-android-warning'></i>Required field.</p> : null }
            </div>
            {this.props.owner? <div className="actions">
              {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/>}
              {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: this.props.section.newSection == true ? null : <DeleteSection sectionId={this.props.section.sectionId} />}
            </div>: <FeatureToggle name="SECTION_ADVICE"> <div className="actions">
               {uiEditMode? <SuggestionBtn onClick={this._saveSuggestionHandler}/>: <EditBtn onClick={this.editHandler}/>}
               {uiEditMode?  <CancelBtn onClick={this.cancelHandler}/>: null }
            </div></FeatureToggle>
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
          <SectionFooter section={this.props.section} owner={this.props.owner} />
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