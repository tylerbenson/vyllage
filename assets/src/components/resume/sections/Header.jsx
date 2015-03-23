var React = require('react');
var actions = require('../actions');

var SectionHeader = React.createClass({
  addSection: function (e) {
    e.preventDefault()
    e.stopPropagation()
    actions.postSection({title: this.props.title.toLowerCase()});
  },
  render: function () {
    return  (
      <div className="row">
        <h4 className="u-pull-left resume-section-title"> {this.props.title}</h4>
        <button 
          className="u-pull-right button button-inverted"
          onClick={this.addSection}>
          Add
        </button>
      </div>
    );
  }
});

module.exports = SectionHeader;