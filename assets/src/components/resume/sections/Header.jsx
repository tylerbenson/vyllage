var React = require('react');

var SectionHeader = React.createClass({
  addSection: function (e) {

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