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
        <div className='row'>
          <h4 className='u-pull-left resume-section-title'>{this.props.title}</h4>
          <a 
            className='button button-inverted u-pull-right'
            onClick={this.editHandler}>
            <i className='icon ion-edit'></i>Edit
          </a>
        </div>
        <p>
          {this.props.section.description}
        </p>
      </div>
    );
  }
});

module.exports = FreeformPreview;