var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');
var AddBtn = require('../../buttons/add');
var DeleteSection = require('../Delete');
var SaveBtn = require('../../buttons/save');
var CancelBtn = require('../../buttons/cancel');
var SectionFooter = require('../sections/Footer');
var Textarea = require('react-textarea-autosize');
var Tag = require('./Tag');
var TagInput = require('./Input');
var MoveButton = require('../../buttons/move');
var SectionFooter = require('../sections/Footer');
var DeleteSection = require('../Delete');
var ConfirmUnload = require('../ConfirmUnload');
var cx = require('react/lib/cx');

var Tags = React.createClass({
  getInitialState: function() {
    return {
      tags: this.props.section.tags,
      uiEditMode: this.props.section.newSection,
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
    var section = this.props.section;
    section.tags = this.state.tags;
    actions.putSection(section);
    this.setState({
      uiEditMode: false
    })
  },
  cancelHandler: function(e) {
    var section = this.props.section;
    if (section.newSection) {
      actions.deleteSection(section.sectionId);
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
  },
  render: function () {
    var uiEditMode = this.state.uiEditMode;
    var content = this.state.tags instanceof Array ? this.state.tags.join(', ') : '';

    var tags = this.state.tags.map(function(tag, index){
      return (
        <Tag key={index} text={tag} onDelete={this.onTagDelete.bind(this, index)} uiEditMode={uiEditMode} />
      );
    }.bind(this));

    var classes = cx({
      'subsection': true
    });


    return (
      <div className={classes}>
        { this.props.owner ? <MoveButton />: null }
        
        <div className='header'>
          {this.props.owner ? <div className="actions">
            {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/> }
            {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: <DeleteSection sectionId={this.props.section.sectionId} />}
          </div>: null}
        </div>
        {this.props.section.sectionId ? <div>
          <div className="tags content">
            {tags}
            {this.state.uiEditMode ? <TagInput onKeyPress={this.onTagAdd} /> : null}
          </div>
          <SectionFooter section={this.props.section} />
          </div>: <p className='content empty'>No {this.props.section.title.toLowerCase()} added yet</p> }
          {this.state.uiEditMode ? <ConfirmUnload onDiscardChanges={this.cancelHandler} /> : null}
      </div>
    );
  }
});

module.exports = Tags;