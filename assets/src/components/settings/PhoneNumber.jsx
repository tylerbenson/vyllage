var React = require('react');
var PrivacySelect = require('./PrivacySelect');

var PhoneNumber = React.createClass({
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              <span>phone number:</span>
            </div>
            <div className="six columns">
              <a>update</a>
            </div>
          </div>
          <div>
            <span>971.800.1565</span>
          </div>
        </div>
        <div className='four columns'>
          <PrivacySelect />
        </div>
      </li>
    );
  }
});

module.exports = PhoneNumber;