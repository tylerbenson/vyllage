var React = require('react');

var Organization = React.createClass({
  render: function () {
    return (
      <li className="row settings-profile-item">
        <div className='nine columns'>
          <span>organization: org name</span>
        </div>
        <div className='three columns'>
          <a className="">change</a>
        </div>
      </li>
    );
  }
});

module.exports = Organization;