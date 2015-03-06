var React = require('react');

var Password = React.createClass({
  render: function () {
    return (
      <li className="row settings-profile-item">
        <div className="nine columns">
          <span>password</span>
        </div>
        <div className="three columns">
          <a className="" href="/account/password/reset">change</a>
        </div>
      </li>
    );
  }
});

module.exports = Password;