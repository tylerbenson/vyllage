var React = require('react');
var PrivacySelect = require('./PrivacySelect');

var Linkedin = React.createClass({
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              linkedin
            </div>
            <div className="six columns">
              <a>remove</a>
            </div>
          </div>
          <div>
            <span>www.linkedin.com/natebenson</span>
          </div>
        </div>
        <div className='four columns'>
          <PrivacySelect />
        </div>
      </li>
    );
  }
});

module.exports = Linkedin;