var React = require('react');

var Password = React.createClass({
  render: function () {
    return (
      <li>
        <span>password</span>
        <a className="u-pull-right">change</a>
      </li>
    );
  }
});

module.exports = Password;