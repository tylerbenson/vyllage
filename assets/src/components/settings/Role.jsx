var React = require('react');

var Role = React.createClass({
  render: function () {
    return (
      <li className="row settings-profile-item">
        <div className='nine columns'>
          <span>role: {this.props.value}</span>
        </div>
        <div className='three columns'>
          <a className="">change</a>
        </div>
      </li>
    );
  }
});

module.exports = Role;