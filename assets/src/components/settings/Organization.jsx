var React = require('react');

var Organization = React.createClass({
  render: function () {
    return (
      <li>
        <span>organization: org name</span>
        <a className="u-pull-right">change</a>
      </li>
    );
  }
});

module.exports = Organization;