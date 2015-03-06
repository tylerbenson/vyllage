var React = require('react');

var Name = React.createClass({
  render: function () {
    return (
      <div className="settings-name">
        <div className='row'>
            <h5>{this.props.value}</h5>
            <a>change</a>
        </div>
        <h6>Member since: Jan 1, 2014</h6>
      </div>
    );
  }
});

module.exports = Name;