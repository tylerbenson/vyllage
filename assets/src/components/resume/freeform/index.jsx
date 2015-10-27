var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');
var DeleteSection = require('../Delete');
var SaveBtn = require('../../buttons/save');
var SuggestionBtn = require('../../buttons/suggestion');
var CancelBtn = require('../../buttons/cancel');
var SectionFooter = require('../sections/Footer');
var Textarea = require('react-textarea-autosize');
var MoveButton = require('../../buttons/move');
var SectionFooter = require('../sections/Footer');
var DeleteSection = require('../Delete');
var ConfirmUnload = require('../ConfirmUnload');
var classnames = require('classnames');
var cloneDeep = require('clone');
var FeatureToggle = require('../../util/FeatureToggle');

var Freeform = React.createClass({
  getInitialState: function() {
    return {
      description: this.props.section.description,
      uiEditMode: this.props.section.newSection,
      error : false
    };
  },
  getDefaultProps: function () {
    return {
      title: '',
      placeholder: 'Tell us more..',
      section: {}
    }
  },
  componentWillReceiveProps: function (nextProps) {
    if( nextProps.section != undefined){
      this.setState({ description : nextProps.section.description });
    }
  },
  componentDidMount: function() {
    this.refs.description.getDOMNode().focus();
  },
  handleChange: function(e) {
    e.preventDefault();
    this.setState({description: e.target.value});
    var section = cloneDeep(this.props.section);
    section.description = e.target.value;
    this.validateSection( section );

  },
  saveHandler: function(e) {
    var section = this.props.section;
    section.description = this.state.description;
    if( this.validateSection( section ) == false ){
      actions.putSection(section);
      this.setState({
        uiEditMode: false
      });
    }
  },
  validateSection : function( section ){
    if( section.description == undefined || section.description.length <= 0 ){
      this.setState({ error : true });
      return true;
    }else{
      this.setState({ error : false });
      return false;
    }
  },
  cancelHandler: function(e) {
    var section = this.props.section;
    if (section.newSection) {
      actions.deleteNewSection();
    } else {
      this.setState({
        description:this.props.section.description,
        uiEditMode: false,
        error : false
      });
    }
  },
  editHandler: function (e) {
    this.setState({
      uiEditMode: true
    }, function () {
      this.refs.description.getDOMNode().focus();
    });
  },
  render: function () {
    var uiEditMode = this.state.uiEditMode;

    var classes = classnames({
      'single': !this.props.isMultiple,
      'subsection': true
    });

    return (
      <div className={classes}>
        { this.props.owner && this.props.isSorting ? <MoveButton />: null }
        <div className='header'>
          {this.props.owner ? <div className="actions">
            {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/> }
            {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: this.props.section.newSection == true ? null : <DeleteSection sectionId={this.props.section.sectionId} />}
          </div>: <FeatureToggle name="SECTION_ADVICE"><div className="actions">
            {uiEditMode? <SuggestionBtn onClick={this._saveSuggestionHandler}/>: <EditBtn onClick={this.editHandler}/>}
            {uiEditMode?  <CancelBtn onClick={this.cancelHandler}/>: null }
          </div></FeatureToggle>
        }
        </div>
        {this.props.section ? <div>
          <div className="content">
            <Textarea
              ref='description'
              disabled={!uiEditMode}
              className={(this.state.error == true ? "error " : "") + "flat"}
              rows="1"
              autoComplete="off"
              placeholder="Tell us more.."
              value={this.state.description}
              onChange={this.handleChange}
            ></Textarea>
            { this.state.error == true ? <p className='error'><i className='ion-android-warning'></i>Required field.</p> : null }
          </div>
          <SectionFooter section={this.props.section} owner={this.props.owner} />
          </div>: <p className='content empty'>No {this.props.section.title.toLowerCase()} added yet</p> }
          {this.state.uiEditMode ? <ConfirmUnload onDiscardChanges={this.cancelHandler} /> : null}
      </div>
    );
  },
  _saveSuggestionHandler : function(){
    var section = cloneDeep(this.props.section);
    section.description = this.state.description;
    actions.saveSectionAdvice(section);
    this.setState({
      description : this.props.section.description,
      uiEditMode: false
    });

  }
});

module.exports = Freeform;