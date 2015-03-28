var React = require('react');
var Preview = require('./Preview');
var Edit = require('./Edit');

var Freeform = React.createClass({
  getDefaultProps: function () {
    return {
      section: {}
    };
  },
  render: function () {
    var uiEditMode = this.props.section.uiEditMode;
    return (
      <div className='section'>
        <div className={this.props.className}>
          {uiEditMode ? <Edit {...this.props} />: <Preview {...this.props}/>}
        </div>
      </div>
    );
  }
});

module.exports = Freeform;