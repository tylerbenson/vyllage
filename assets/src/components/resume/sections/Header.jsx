var React = require('react');
var actions = require('../actions');

var SectionHeader = React.createClass({
  addSection: function (e) {
    e.preventDefault()
    e.stopPropagation()
    actions.postSection({type: this.props.type}, {});
  },
  render: function () {
    return  (
      <div className="u-pull-left full">
        <button className="u-pull-left article-btn"> {this.props.title} </button>
        <button className="u-pull-right article-btn addSection-btn"
                onClick={this.addSection}>
                {this.props.title}
        </button>
      </div>
    );
  }
});

module.exports = SectionHeader;