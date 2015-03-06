var React = require('react');
var PrivacySelect = require('./PrivacySelect');

var Address = React.createClass({
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              address:
              <address dangerouslySetInnerHTML={{__html: this.props.value}}>
              </address>
            </div>
            <div className="six columns">
              <a>update</a>
            </div>
          </div>
        </div>
        <div className='four columns'>
          <PrivacySelect organization={this.props.organization} />
        </div>
      </li>
    );
  }
});

module.exports = Address;