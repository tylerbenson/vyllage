var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');
var DeleteSection = require('../Delete');
var SaveBtn = require('../../buttons/save');
var SuggestionBtn = require('../../buttons/suggestion');
var CancelBtn = require('../../buttons/cancel');
var SectionFooter = require('../sections/Footer');
var Textarea = require('react-textarea-autosize');
var Tag = require('./Tag');
var TagInput = require('./Input');
var MoveButton = require('../../buttons/move');
var SectionFooter = require('../sections/Footer');
var DeleteSection = require('../Delete');
var ConfirmUnload = require('../ConfirmUnload');
var classnames = require('classnames');
var Sortable = require('../../util/Sortable');
var resumeActions = require('../actions');
var cloneDeep = require('clone-deep');
var FeatureToggle = require('../../util/FeatureToggle');


var Tags = React.createClass({
  getInitialState: function() {
    return {
      tags: this.props.section.tags,
      uiEditMode: this.props.section.newSection,
      error : false
    };
  },
  getDefaultProps: function () {
    return {
      title: '',
      placeholder: 'Input tags separated by comma..',
      section: {}
    }
  },
  componentWillReceiveProps: function (nextProps) {
    this.setState({
      tags: nextProps.section.tags,
    });
  },
  handleChange: function(e) {
    e.preventDefault();
    var input = e.target.value.trim().split(',');
    var tags = [];

    for(var i=0;i<input.length;i++) {
      var tag = input[i].trim();
      if(tag.length > 0) {
        tags.push(tag);
      }
    }
    this.setState({tags: tags});
  },
  saveHandler: function(e) {
    var tags = [];
    var tagRef = this.refs.tags.getDOMNode();

    jQuery(tagRef).find('.tag').each(function(index) {
      if( jQuery(this).attr("rel") )
        tags.push(jQuery(this).attr("rel") );
    });
    var temp_value = jQuery(tagRef).find('input').val();
    if( temp_value.length > 0 ){
      tags.push(temp_value);
    }
    var section = this.props.section;
    section.tags = tags;

    if( this.validateSection( section ) == false ){
      actions.putSection(section);
      this.setState({
        tags: tags,
        uiEditMode: false
      });
    }

  },
  validateSection : function( section ){
    if( section.tags == undefined || section.tags.length <= 0 ){
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
        tags:this.props.section.tags,
        uiEditMode: false
      });
    }
  },
  editHandler: function (e) {
    this.setState({
      uiEditMode: true
    });
  },
  onTagDelete: function (i) {
    var temp = this.state.tags.slice();
    temp.splice(i,1);
    this.setState({
      tags: temp
    });
  },
  onTagAdd: function(e) {
    if(e.which === 13) {
      var temp = this.state.tags.slice();
      temp.push(e.target.value);

      this.setState({
        tags: temp
      });

      e.target.value = "";
    }

    var section = {}
    section.tags = this.state.tags.length > 0 ?  this.state.tags : temp ;
    this.validateSection(section);

  },
  start: function(event, ui) {
    ui.placeholder.width(ui.item.width());
  },
  render: function () {
    var uiEditMode = this.state.uiEditMode;
    var content = this.state.tags instanceof Array ? this.state.tags.join(', ') : '';

    var tags = this.state.tags.map(function(tag, index){
      return (
        <Tag key={index} text={tag} onDelete={this.onTagDelete.bind(this, index)} uiEditMode={uiEditMode} />
      );
    }.bind(this));

    var classes = classnames({
      'single': !this.props.isMultiple,
      'subsection': true
    });

    var config = {
        list: ".move-tag",
        items: "div.tag",
        stop: this.stop,
        start: this.start,
      };

    return (
      <div ref="tags" className={classes}>
        { this.props.owner ? <MoveButton />: null }
        <div className='header'>
          {this.props.owner ? <div className="actions">
            {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/> }
            {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: this.props.section.newSection == true ? null : <DeleteSection sectionId={this.props.section.sectionId} />}
          </div>: <FeatureToggle name="SECTION_ADVICE"> <div className="actions">
            {uiEditMode? <SuggestionBtn onClick={this._saveSuggestionHandler}/>: <EditBtn onClick={this.editHandler}/>}
            {uiEditMode?  <CancelBtn onClick={this.cancelHandler}/>: null }
          </div></FeatureToggle>
        }
        </div>
        {this.props.section ? <div>

          { this.state.uiEditMode == undefined || this.state.uiEditMode == false ? <div className="tags content">{tags}</div> :
          <Sortable config={config} className="tags content move-tag">
            {tags}
            {this.state.uiEditMode ? <TagInput className={(tags.length < 1 ? "error " : "") + "inline flat"} onKeyPress={this.onTagAdd} /> : null}
            { tags.length < 1 ? <p className='error'><i className='ion-android-warning'></i>Required field.</p> : null }
          </Sortable> }


          <SectionFooter section={this.props.section} owner={this.props.owner} />
          </div>: <p className='content empty'>No {this.props.section.title.toLowerCase()} added yet</p> }
          {this.state.uiEditMode ? <ConfirmUnload onDiscardChanges={this.cancelHandler} /> : null}
      </div>
    );
  },
  _saveSuggestionHandler : function(){
   var tags = [];
    var tagRef = this.refs.tags.getDOMNode();

    jQuery(tagRef).find('.tag').each(function(index) {
      if( jQuery(this).attr("rel") )
        tags.push(jQuery(this).attr("rel") );
    });
    var temp_value = jQuery(tagRef).find('input').val();
    if( temp_value.length > 0 ){
      tags.push(temp_value);
    }
    var section = cloneDeep(this.props.section);
    section.tags = tags;
    actions.saveSectionAdvice(section);

    this.setState({
      tags: this.props.section.tags,
      uiEditMode: false
    })
  }
});

module.exports = Tags;