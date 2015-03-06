var React = require('react');
var PrivacySelect = require('./PrivacySelect');

var Twitter = React.createClass({
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              twitter:
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
          <PrivacySelect />
        </div>
      </li>
    );
  }
});

module.exports = Twitter;