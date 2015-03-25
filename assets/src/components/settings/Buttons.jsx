var React = require('react');

var Buttons = React.createClass({
  render: function () {
    return (
      <div className='row'>
        <button className='u-pull-right' onClick={this.props.cancel}>Cancel</button>
        <button className='u-pull-right' onClick={this.props.save}>Save</button>
      </div>
    );
  }
});

module.exports = Buttons;