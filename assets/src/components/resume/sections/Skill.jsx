var React = require('react');
var Header = require('./Header');
var Freeform = require('../freeform');

var Skills = React.createClass({
  render: function () {
    return (
      <div className='section'>
        <div className="container">
          <Freeform title='Skills' section={this.props.section} owner={this.props.owner} />
        </div>
      </div>
    );
  }
});

module.exports = Skills;