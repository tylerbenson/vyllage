var React = require('react');

var SharedLinks = React.createClass({
  render: function () {
    return (
       <li className="row settings-profile-item">
          <div className="nine columns">
            <span>shared links</span>
          </div>
          <div className="three columns">
            <a className="">reset all</a>
          </div>
        </li>
    );
  }
});

module.exports = SharedLinks;