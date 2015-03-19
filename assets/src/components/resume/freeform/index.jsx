var React = require('react');
var Preview = require('./Preview');
var Edit = require('./Edit');

var Freeform = React.createClass({
  getDefaultProps: function () {
    return {
      className: 'freeform',
      section: {}
    };
  },
  render: function () {
    var uiEditMode = this.props.sections.uiEditMode;
    return (
      <div className={this.props.className}>
        {uiEditMode ? <Edit {...this.props} />: <Preview {...this.props}/>}
      </div>
    );
  }
});

module.exports = Freeform;