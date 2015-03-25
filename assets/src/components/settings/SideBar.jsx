var React = require('react');
var actions = require('./actions');

var SideBar = React.createClass({
  select: function (type, e) {
    actions.setSettingsType(type);
  },
  render: function () {
    return (
      <div>
        <ul>
          <li onClick={this.select.bind(this, 'profile')}>
            <i className='icon ion-person'></i><a>PROFILE</a>
          </li>
          <li onClick={this.select.bind(this, 'account')}>
            <i className='icon ion-key'></i><a>ACCOUNT</a>
          </li>
          <li onClick={this.select.bind(this, 'social')}>
            <i className='icon ion-social-buffer'></i><a>SOCIAL</a>
          </li>
        </ul>
      </div>
    );
  }
});

module.exports = SideBar;