var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');
var AddBtn = require('../../buttons/add');
var DeleteSection = require('../Delete');
var SaveBtn = require('../../buttons/save');
var CancelBtn = require('../../buttons/cancel');
var SectionFooter = require('../sections/Footer');
var Textarea = require('react-textarea-autosize');
var MoveButton = require('../../buttons/move');
var { DragDropMixin } = require('react-dnd');
var {dragSource, dropTarget} = require('../sections/sectionDragDrop');
var SectionFooter = require('../sections/Footer');
var DeleteSection = require('../Delete');
var ConfirmUnload = require('../ConfirmUnload');
var cx = require('react/lib/cx');

var Tags = React.createClass({
  mixins: [DragDropMixin],
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
  statics: {
    configureDragDrop(register) {
      register('section', {
        dragSource,
        dropTarget
      });
    }
  },
  componentWillReceiveProps: function (nextProps) {
    this.setState({
      tags: nextProps.section.tags,
    });
  },
  componentDidMount: function() {
    this.refs.tags.getDOMNode().focus();
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
    }, function () {
      this.refs.tags.getDOMNode().focus();
    });
  },
  render: function () {
    var uiEditMode = this.state.uiEditMode;
    var isDragging = this.getDragState('section').isDragging;
    var isHovering = this.getDropState('section').isHovering;
    var content = this.state.tags instanceof Array ? this.state.tags.join(', ') : '';

    var classes = cx({
      'dragged': isDragging,
      'hovered': isHovering,
      'subsection': true
    });

    console.log( this.props.owner);

    return (
      <div className={classes} {...this.dropTargetFor('section')}>
        {/* this.props.owner ? <MoveButton {...this.dragSourceFor('section')} />: null  */}
        { this.props.owner ? <MoveButton />: null }
        <div className='header'>
          {this.props.owner ? <div className="actions">
            {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/> }
            {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: <DeleteSection sectionId={this.props.section.sectionId} />}
          </div>: null}
        </div>
        {this.props.section.sectionId ? <div>
          <div className="content">
            <Textarea
              ref='tags'
              disabled={!uiEditMode}
              className="flat"
              rows="1"
              autoComplete="off"
              placeholder="Input tags separated by comma."
              defaultValue={content}
              onChange={this.handleChange}
            ></Textarea>
          </div>
          <SectionFooter section={this.props.section} />
          </div>: <p className='content empty'>No {this.props.section.title.toLowerCase()} added yet</p> }
          {this.state.uiEditMode ? <ConfirmUnload onDiscardChanges={this.cancelHandler} /> : null}
      </div>
    );
  }
});

module.exports = Tags;