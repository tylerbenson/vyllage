var React = require('react');
var actions = require('../actions');

var FreeformPreview = React.createClass({
  editHandler: function (e) {
    e.preventDefault();
    actions.enableEditMode(this.props.section.sectionId);
  },
  render: function () {
    var className = this.props.className + '-preview';
    return (
      <div className={className}>
        <a className='button' onClick={this.editHandler}>Edit</a>
        <p>
          {this.props.description}
        </p>
      </div>
    );
  }
});

module.exports = FreeformPreview;