var React = require('react');
var Preview = require('./Preview');
var Edit = require('./Edit');
var MoveBtn = require('../../buttons/move');

var Freeform = React.createClass({
  getDefaultProps: function () {
    return {
      className: 'freeform',
      section: {}
    };
  },
  render: function () {
    var uiEditMode = this.props.section.uiEditMode;
    return (
      <div className ="row">
        <div className="twelve columns move-container">
          <MoveBtn />
        </div>
        <div className={this.props.className}>
          {uiEditMode ? <Edit {...this.props} />: <Preview {...this.props}/>}
        </div>
      </div>
    );
  }
});

module.exports = Freeform;