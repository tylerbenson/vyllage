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
    return {
      description: this.props.section.description,
      uiEditMode: false
    };
  },
  getDefaultProps: function () {
    return {
      placeholder: 'Tell us more..',
      section: {}
    }
  },
  componentWillReceiveProps: function (nextProps) {
    this.setState({
      description: nextProps.section.description,
    });
  },
  componentDidMount: function () {
    this.refs.description.getDOMNode().focus();
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
    this.setState({
      description:this.props.section.description,
      uiEditMode: false
    });
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
    return (
      <div className='section'>
        <div className='header'>
          <div className='title'>
            <h1>{this.props.title}</h1>
          </div>
          <div className="actions">
            {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/>}
            {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: ''}
          </div>
        </div>
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
      </div>
    );
  }
});

module.exports = Freeform;