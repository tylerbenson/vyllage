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

module.exports = Linkedin;