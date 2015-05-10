var React = require('react');
var actions = require('../actions');
var AddBtn = require('../../buttons/add');

var SectionHeader = React.createClass({
  addSection: function (e) {
    actions.postSection({
      title: this.props.title.toLowerCase(),
      type: this.props.type
    });
  },
  render: function () {
    // var add = (this.props.type !== 'freeform');
    return  (
      <div className='header'>
        <div className='title'>
          <h1>{this.props.title}</h1>
        </div>
        {this.props.owner ? <div className="actions">
          <AddBtn onClick={this.addSection} /> 
        </div>: null}
      </div>
    );
  }
});

module.exports = SectionHeader;