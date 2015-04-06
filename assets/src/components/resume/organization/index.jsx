var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');
var DeleteSection = require('../Delete');
var SaveBtn = require('../../buttons/save');
var CancelBtn = require('../../buttons/cancel');
var Textarea = require('react-textarea-autosize');
var Datepicker = require('../../datepicker');

var Organization = React.createClass({
  getInitialState: function () {
    return {
      section: this.props.section,
    };
  },
  handleChange: function(key, e) {
    // e.preventDefault();
    var section = this.state.section;
    section[key] = e.target.value;
    this.setState({section: section});
  },
  saveHandler: function(e) {
    var section = this.state.section;
    section.uiEditMode = false;
    actions.putSection(section);
  },
  cancelHandler: function(e) {
    this.setState({section:this.props.section});
    actions.disableEditMode(this.props.section.sectionId);
  },
  editHandler: function (e) {
    actions.enableEditMode(this.props.section.sectionId);
  },
  render: function () {
    var uiEditMode = this.props.section.uiEditMode;
    var section = this.props.section;
    return (
      <div className ="subsection">
        <div className='header'>
          <div className='title'>
            <h2>
              <input 
                disabled={!uiEditMode}
                className='flat'
                type='text'
                defaultValue={section.organizationName}
                onChange={this.handleChange.bind(this, 'organizationName')}
             />
            </h2>
          </div>
          <div className="actions">
            {uiEditMode? <SaveBtn onClick={this.saveHandler}/>: <EditBtn onClick={this.editHandler}/>}
            {uiEditMode? <CancelBtn onClick={this.cancelHandler}/>: <DeleteSection sectionId={this.props.section.sectionId} />}
          </div>
        </div>
        <div className='content'>
          <Textarea
            disabled={!uiEditMode}
            className="flat"
            rows="1"
            autoComplete="off"
            placeholder="Company Description"
            defaultValue={section.organizationDescription}
            onChange={this.handleChange.bind(this, 'organizationDescription')}
          ></Textarea>
          <section className="subsubsection">
            <div className="header">
              <div className="title">
                <h3>
                  <input
                    disabled={!uiEditMode}
                    className="flat"
                    type="text"
                    placeholder="Position"
                    defaultValue={section.role}
                    onChange={this.handleChange.bind(this, 'role')}
                  />
                </h3>
              </div>
            </div>
            <div className="content">
              <Datepicker 
                name='startDate'
                date={section.startDate}
                setDate={this.handleChange}
              >
                <input 
                  disabled={!uiEditMode}
                  type="text"
                  className="inline flat date"
                  placeholder="Start Date"
                />
              </Datepicker>  
              -
              <Datepicker 
                name='endDate'
                date={section.endDate}
                setDate={this.handleChange}
              >
                <input 
                  disabled={!uiEditMode}
                  type="text"
                  className="inline flat date"
                  placeholder="End Date"
                />
              </Datepicker>  
              <input 
                disabled={!uiEditMode}
                type="text" 
                className="flat location"
                placeholder="Location"
                defaultValue={section.location}
                onChange={this.handleChange.bind(this, 'location')}
              />
              <Textarea
                disabled={!uiEditMode}
                className="flat"
                rows="3"
                placeholder="Note at least three (3) notable accomplishments achieved during this position.."
                defaultValue={section.highlights}
                onChange={this.handleChange.bind(this, 'highlights')}
              ></Textarea>
            </div>
          </section>  
        </div>
      </div>
    );
  }
});

module.exports = Organization;