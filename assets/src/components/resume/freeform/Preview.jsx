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
            <p className='u-pull-left'>{this.props.title}</p>
            <p className='u-pull-right'><EditBtn editHandler={this.editHandler}/></p>
          </div>
        </div>
       <div className='row'>
         <div className="twelve columns">
            <p>
              {this.props.section.description}
            </p>
          </div>
        </div>
      </div>
    );
  }
});

module.exports = FreeformPreview;