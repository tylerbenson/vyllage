var React = require('react');
var Header = require('./Header');
var Freeform = require('../freeform');
var MoveButton = require('../../buttons/move');

var Skills = React.createClass({
  render: function () {
    return (
      <div className='section'>
        <div className="container">
          <MoveButton />
          <Freeform title='Skills' section={this.props.section} owner={this.props.owner} />
        </div>
      </div>
    );
  }
});

module.exports = Skills;