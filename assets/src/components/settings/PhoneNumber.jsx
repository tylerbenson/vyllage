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
            {this.props.value}
          </div>
        </div>
        <div className='four columns'>
          <PrivacySelect organization={this.props.organization} />
        </div>
      </li>
    );
  }
});

module.exports = PhoneNumber;