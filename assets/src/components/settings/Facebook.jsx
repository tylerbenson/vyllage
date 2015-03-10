var React = require('react');
var PrivacySelect = require('./PrivacySelect');

var Facebook = React.createClass({
  getInitialState: function () {
    return { edit: false };
  },
  editHandler: function (e) {
    e.preventDefault();
    this.setState({edit: true});
  },
  valueHandler: function (e) {
    e.preventDefault();
    this.props.changeSetting('facebook', {value: e.target.value, privacy: this.props.privacy});
  },
  privacyHandler: function (e) {
    e.preventDefault();
    this.props.changeSetting('facebook', {value: this.props.value, privacy: e.target.value});
  },
  keyPress: function (e) {
    e.stopPropagation();
    if (e.key === 'Enter') {
      this.setState({edit: false});
    }
  },
  renderForm: function () {
    return <input 
              type='text'
              className='u-full-width'
              autoFocus
              value={this.props.value}
              onKeyPress={this.keyPress}
              onChange={this.valueHandler} />
  }, 
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              facebook.com
            </div>
            <div className="six columns">
              <a onClick={this.editHandler}>update</a>
            </div>
          </div>
          <div>
            {this.state.edit? this.renderForm(): this.props.value}
          </div>
        </div>
        <div className='four columns'>
          <PrivacySelect 
            value={this.props.privacy}
            organization={this.props.organization}
            onChange={this.privacyHandler} />
        </div>
      </li>
    );
  }
});

module.exports = Facebook;