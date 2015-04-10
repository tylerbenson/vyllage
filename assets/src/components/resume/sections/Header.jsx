var React = require('react');
var actions = require('../actions');
var AddBtn = require('../../buttons/add');

var SectionHeader = React.createClass({
  addSection: function (e) {
    actions.postSection({
      title: this.props.title.toLowerCase(),
    });
  },
  render: function () {
    return  (
      <div className='header'>
        <div className='title'>
          <h1>{this.props.title}</h1>
        </div>
        <div className="actions">
          <AddBtn onClick={this.addSection} /> 
        </div>
      </div>
    );
  }
});

module.exports = SectionHeader;