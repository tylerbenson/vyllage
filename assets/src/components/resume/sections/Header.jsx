var React = require('react');
var actions = require('../actions');
var AddBtn = require('../../buttons/add');

var SectionHeader = React.createClass({
  addSection: function (e) {
    actions.postSection({title: this.props.title.toLowerCase()});
  },
  render: function () {
    return  (
      <div className='header'>
        <h1 className='u-pull-left'>{this.props.title}</h1>
        <div className="pull right actions">
          <AddBtn onClick={this.addSection} /> 
        </div>
      </div>
    );
  }
});

module.exports = SectionHeader;