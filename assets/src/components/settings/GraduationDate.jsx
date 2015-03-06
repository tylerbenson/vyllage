var React = require('react');

var GraduationDate = React.createClass({
  render: function () {
    return (
      <li className="row settings-profile-item">
        <div className='nine columns'>
          <span>graduation data: Aug, 1, 2015</span>
        </div>
        <div className='three columns'>
          <a className="">change</a>
        </div>
      </li>
    );
  }
});

module.exports = GraduationDate;