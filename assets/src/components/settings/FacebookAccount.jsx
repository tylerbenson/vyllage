var React = require('react');

var FacebookAccount = React.createClass({
  render: function () {
    return (
      <li className="row settings-profile-item">
        <div className="nine columns">
          <span>facebook account: linkedin</span>
        </div>
        <div className='three columns'>
          <a className="">remove</a>
        </div>
      </li>
    );
  }
});

module.exports = FacebookAccount;