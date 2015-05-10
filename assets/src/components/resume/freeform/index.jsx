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

var Freeform = React.createClass({
  mixins: [DragDropMixin],
  getInitialState: function() {
    return {
      description: this.props.section.description,
      uiEditMode: this.props.section.newSection,
    };
  },
  getDefaultProps: function () {
    return {
      title: '',
      placeholder: 'Tell us more..',
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
      description: nextProps.section.description,
    });
  },
  componentDidMount: function() {
    // this.refs.description.getDOMNode().focus();
  },
  handleChange: function(e) {
    e.preventDefault();
    this.setState({description: e.target.value});
  },
  saveHandler: function(e) {
    var section = this.props.section;
    section.description = this.state.description;
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
        description:this.props.section.description,
        uiEditMode: false
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
    var { isDragging } = this.getDragState('section');
    var opacity = isDragging ? 0 : 1;
    return (
      <div className='subsection' {...this.dropTargetFor('section')} style={{opacity}}>
        <MoveButton {...this.dragSourceFor('section')} />
        <div className='header'>
          {this.props.owner ? <div className="actions">
            {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/> }
            {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: <DeleteSection sectionId={this.props.section.sectionId} />}
          </div>: null}
        </div>
        {this.props.section.sectionId ? <div>
          <div className="content">
            <Textarea
              ref='description'
              disabled={!uiEditMode}
              className="flat"
              rows="1"
              autoComplete="off"
              placeholder="Tell us more.."
              value={this.state.description}
              onChange={this.handleChange}
            ></Textarea>
          </div>
          <SectionFooter section={this.props.section} />
          </div>: <p className='content empty'>No {this.props.section.title.toLowerCase()} added yet</p> }
      </div>
    );
  }
});

module.exports = Freeform;