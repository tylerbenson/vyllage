var React = require('react');

var SharedLinks = React.createClass({
  render: function () {
    return (
       <li>
          <span>shared links</span>
          <a className="u-pull-right">reset all</a>
        </li>
    );
  }
});

module.exports = SharedLinks;