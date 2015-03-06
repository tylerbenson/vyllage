var React = require('react');

var FacebookAccount = React.createClass({
  render: function () {
    return (
      <li>
        <span>facebook account: linkedin</span>
        <a className="u-pull-right">remove</a>
      </li>
    );
  }
});

module.exports = FacebookAccount;