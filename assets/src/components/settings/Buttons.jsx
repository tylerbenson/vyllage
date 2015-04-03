var React = require('react');

var Buttons = React.createClass({
  render: function () {
    return (
      <div className='actions'>
        <button onClick={this.props.save}> <i className="ion-checkmark"></i> Save</button>
        <button className='secondary inverted' onClick={this.props.cancel}>Cancel</button>
      </div>
    );
  }
});

module.exports = Buttons;