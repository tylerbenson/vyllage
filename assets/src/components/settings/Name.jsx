var React = require('react');

var Name = React.createClass({
  render: function () {
    return (
      <div className="settings-title">
        <h5>Nathon Benson</h5>
        <a>change</a>
        <h6>Member since: Jan 1, 2014</h6>
      </div>
    );
  }
});

module.exports = Name;