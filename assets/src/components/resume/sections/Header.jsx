var React = require('react');
var actions = require('../actions');
var AddBtn = require('../../buttons/add');
var MoveButton = require('../../buttons/move');
var filter = require('lodash.filter');
var sections = require('../../sections');

var SectionHeader = React.createClass({
  addSection: function (e) {
    actions.postSection({
      title: this.props.title,
      type: this.props.type,
    //  sectionPosition: this.props.groupPosition
    });
  },
  render: function () {
    var sectionSpec = filter(sections, {type: this.props.type});
    var isMultiple = sectionSpec instanceof Array ? sectionSpec[0].isMultiple : false;
    var sectionHeader = this.props.title;
    if( sectionHeader == "summary" ) sectionHeader = "Objective";
    if( sectionHeader == "Job Experience" ) sectionHeader = "Experience";


    return  (
      <div className='header'>
        <div className='title'>
          <h1>{sectionHeader}</h1>
        </div>
        {this.props.owner && isMultiple ? <div className="actions">
          <AddBtn onClick={this.addSection} />
        </div>: null}
      </div>
    );
  }
});

module.exports = SectionHeader;