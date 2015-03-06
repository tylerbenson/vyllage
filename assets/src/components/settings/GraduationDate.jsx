var React = require('react');

var GraduationDate = React.createClass({
  render: function () {
    return (
      <li>
        <span>graduation data: Aug, 1, 2015</span>
        <a className="u-pull-right">change</a>
      </li>
    );
  }
});

module.exports = GraduationDate;