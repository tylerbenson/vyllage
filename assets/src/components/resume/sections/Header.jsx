var React = require('react');
var actions = require('../actions');
var AddBtn = require('../../buttons/add');
var MoveBtn = require('../../buttons/move');

var SectionHeader = React.createClass({
  addSection: function (e) {
    actions.postSection({title: this.props.title.toLowerCase()});
  },
  render: function () {
    return  (
       <div className='row'>
          <div className="twelve columns move-container">
            <MoveBtn />
          </div>
          <div className="twelve columns header-section-title">
            <p className='u-pull-left'>{this.props.title}</p>
            <p className='u-pull-right'><AddBtn addSection={this.addSection}/></p>
          </div>
        </div>
    );
  }
});

module.exports = SectionHeader;