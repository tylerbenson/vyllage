var React = require('react');
var Edit = require('./Edit');
var Preview = require("./Preview");

var Organization = React.createClass({
  getDefaultProps: function () {
    return {
      className: 'organization',
      section: {}
    };
  },
  render: function () {
    var uiEditMode = this.props.section.uiEditMode;
    return (
      <div className ="subsection">
        <div className={this.props.className}>
          {uiEditMode ? <Edit {...this.props} />: <Preview {...this.props}/>}
        </div>
      </div>
    );
  }
});

module.exports = Organization;