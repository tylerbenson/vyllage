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
var classnames = require('classnames');
var cloneDeep = require('clone-deep');
var validator = require('validator');

var Project = React.createClass({
  getInitialState: function () {
    return {
      section: cloneDeep(this.props.section),
      uiEditMode: this.props.section.newSection,
      newSection: this.props.section.newSection,
      error : false
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
    this.validateSection(section);
  },
  toggleCurrent: function () {
    var section = this.state.section;
    section.isCurrent = !section.isCurrent;
    this.setState({section: section});
  },
  saveHandler: function(e) {
    var section = this.state.section;
    if( this.validateSection( section ) == false ){
      actions.putSection(section);
      this.setState({
        section: section,
        uiEditMode: false
      });
    }
  },
  validateSection : function( section ){
    if( section.projectTitle == undefined || section.projectTitle.length <= 0 ){
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
      actions.deleteSection(section.sectionId);
    } else {
      this.setState({
        section: section,
        uiEditMode: false,
        error : false
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
    var projectUrl = section.projectUrl ? section.projectUrl : '';

    if(validator.isURL(projectUrl)) {
      //Does not have protocol
      if(projectUrl.match(/http/) === null){
        projectUrl = '//' + projectUrl;
      }
    }
    else {
      projectUrl = null;
    }

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
                { uiEditMode ?
                  <Textarea
                    ref='projectTitle'
                    disabled={!uiEditMode}
                    className={(this.state.error == true ? "error " : "") + "flat"}
                    style={uiEditMode || section.projectTitle ? {}: {display: 'none'}}
                    placeholder='Project Title'
                    type='text'
                    value={section.projectTitle}
                    rows="1"
                    onChange={this.handleChange.bind(this, 'projectTitle')}
                  />
                  :
                  <span>
                    { section.projectTitle }
                    { projectUrl !== null ?
                      <a className="flat secondary icon link button"
                        href={projectUrl}
                        target="_blank"
                        title={section.projectUrl}>
                          <i className="ion-link"></i>
                      </a>
                    : null }
                  </span>
                }
              </h2>
               { this.state.error == true ? <p className='error'><i className='ion-android-warning'></i>Required field.</p> : null }
              { uiEditMode ?
                <input className="flat link"
                  ref='projectUrl'
                  disabled={!uiEditMode}
                  placeholder='Project URL'
                  type='text'
                  value={section.projectUrl}
                  onChange={this.handleChange.bind(this, 'projectUrl')}
                />
                : null }
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