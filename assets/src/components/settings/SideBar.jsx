var React = require('react');


var SideBar = React.createClass({
  render: function () {
    return (
      <div>
        <ul>
          <li><i className='icon ion-person'></i> <a>PROFILE</a></li>
          <li><i className='icon ion-key'></i> <a>ACCOUNT</a></li>
          <li><i className='icon ion-social-buffer'></i> <a>SOCIAL</a></li>
        </ul>
      </div>
    );
  }
});

module.exports = SideBar;