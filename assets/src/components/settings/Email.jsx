var React = require('react');
var PrivacySelect = require('./PrivacySelect');

var Email = React.createClass({
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className='six columns'>
              <span>email address:</span>
            </div>
            <div className="six columns">
              <a>change/add</a>
            </div>
          </div>
          <div>
            <div className="six columns">
              <span>nben888@gmail.com</span>
            </div>
            <div className="six columns">
              <a>primary</a>
            </div>
          </div>
        </div>
        <div className='four columns'>
          <PrivacySelect />
        </div>
      </li>
    );
  }
});

module.exports = Email;