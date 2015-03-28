var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');
var DeleteSection = require('../Delete');

var FreeformPreview = React.createClass({
  editHandler: function (e) {
    actions.enableEditMode(this.props.section.sectionId);
  },
  render: function () {
    return (
      <div>
        <div className='header'>
         <h1>{this.props.title}</h1>
         <div className="pull right actions">
            <EditBtn editHandler={this.editHandler}/>
            <DeleteSection sectionId={this.props.section.sectionId} />
          </div>
        </div>
        <p>
          {this.props.section.description}
        </p>
      </div>
    );
  }
});

module.exports = FreeformPreview;