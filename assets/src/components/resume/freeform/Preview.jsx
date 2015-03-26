var React = require('react');
var actions = require('../actions');
var EditBtn = require('../../buttons/edit');

var FreeformPreview = React.createClass({
  editHandler: function (e) {
    actions.enableEditMode(this.props.section.sectionId);
  },
  render: function () {
    var className = this.props.className + '-preview';
    return (
      <div className={className}>
        <div className='row'>
         <div className="twelve columns section-title">
            <p className='u-pull-right'><EditBtn editHandler={this.editHandler}/></p>
          </div>
        </div>
        <p>
          {this.props.section.description}
        </p>
      </div>
    );
  }
});

module.exports = FreeformPreview;