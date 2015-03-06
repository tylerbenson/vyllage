var React = require('react');
var PrivacySelect = require('./PrivacySelect');

var Other = React.createClass({
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              other
            </div>
            <div className="six columns">
              <a>add</a>
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

module.exports = Other;