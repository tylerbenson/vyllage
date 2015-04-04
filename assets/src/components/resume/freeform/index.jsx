var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');
var DeleteSection = require('../Delete');
var SaveBtn = require('../../buttons/save');
var CancelBtn = require('../../buttons/cancel');
var SectionFooter = require('../sections/Footer');
var Textarea = require('react-textarea-autosize');

var Freeform = React.createClass({
  getInitialState: function() {
    return {description: this.props.section.description}; 
  },
  getDefaultProps: function () {
    return {
      placeholder: 'tell us ....',
      section: {}
    }
  },
  handleChange: function(e) {
    e.preventDefault();
    this.setState({description: e.target.value});
  },
  saveHandler: function(e) {
    var section = this.props.section;
    section.description = this.state.description;
    section.uiEditMode = false;
    actions.putSection(section);
  },
  cancelHandler: function(e) {
    this.setState({description:this.props.section.description});
    actions.disableEditMode(this.props.section.sectionId);
  },
  editHandler: function (e) {
    actions.enableEditMode(this.props.section.sectionId);
  },
  render: function () {
    var uiEditMode = this.props.section.uiEditMode;
    return (
      <div className='section'>
        <div className='header'>
          <div className='title'>
            <h1>{this.props.title}</h1>
          </div>
          <div className="actions">
            {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/>}
            {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: <DeleteSection sectionId={this.props.section.sectionId} />}
          </div>
        </div>
        <div className="content">
          <Textarea
            key={this.props.section.description || undefined}
            disabled={!uiEditMode}
            className="flat"
            rows="1"
            autoComplete="off"
            placeholder="Tell us more ..."
            defaultValue={this.props.section.description}
            onChange={this.handleChange}
          ></Textarea>
        </div>
        <SectionFooter section={this.props.section} />
      </div>
    );
  }
});

module.exports = Freeform;