var React = require('react');
var actions = require('../actions');
var AddBtn = require('../../buttons/add');
var filter = require('lodash.filter');

var SectionHeader = React.createClass({
  addSection: function (e) {
    actions.postSection({
      title: this.props.title.toLowerCase(),
      type: this.props.type,
      sectionPosition: this.props.groupPosition
    });
  },
  render: function () {
    var sections = filter(this.props.sections, {title: 'summary'});
    var showAdd = !((this.props.title === 'summary') && (sections.length > 0));
    return  (
      <div className='header'>
        <div className='title'>
          <h1>{this.props.title}</h1>
        </div>
        {this.props.owner && showAdd ? <div className="actions">
          <AddBtn onClick={this.addSection} /> 
        </div>: null}
      </div>
    );
  }
});

module.exports = SectionHeader;