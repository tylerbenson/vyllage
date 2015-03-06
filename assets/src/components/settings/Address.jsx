var React = require('react');
var PrivacySelect = require('./PrivacySelect');

var Address = React.createClass({
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              <address>
                address:<br />
                1906 NE 151st Cir <br />
                Unit 15b <br />
                Vancovuer, WA 98686 <br />
              </address>
            </div>
            <div className="six columns">
              <a>update</a>
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

module.exports = Address;