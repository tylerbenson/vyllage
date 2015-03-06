var React = require('react');

var Role = React.createClass({
  render: function () {
    return (
      <li>
        <span>role: student</span>
        <a className="u-pull-right">change</a>
      </li>
    );
  }
});

module.exports = Role;