var React = require('react');
var PrivacySelect = require('./PrivacySelect');

var Facebook = React.createClass({
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              facebook.com
            </div>
            <div className="six columns">
              <a>reset all</a>
            </div>
          </div>
          <div>
            <span>www.facebook.com/natebenson</span>
          </div>
        </div>
        <div className='four columns'>
          <PrivacySelect />
        </div>
      </li>
    );
  }
});

module.exports = Facebook;