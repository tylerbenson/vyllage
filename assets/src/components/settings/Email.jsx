var React = require('react');
var PrivacySelect = require('./PrivacySelect');
var Actions = require('./actions');
var validator = require('validator');

var Email = React.createClass({
  getInitialState: function () {
    return { 
      edit: false,
      error:false
    };
  },
  editHandler: function (e) {
    e.preventDefault();
    this.setState({edit: !this.state.edit});
  },
  valueHandler: function (e) {
    e.preventDefault();
    Actions.changeSetting('email', {value: e.target.value, privacy: this.props.privacy});
  },
  privacyHandler: function (e) {
    e.preventDefault();
    Actions.changeSetting('email', {value: this.props.value, privacy: e.target.value});
  },
  keyPress: function (e) {
    e.stopPropagation();
    if (e.key === 'Enter') {
      this.setState({
        edit: false
      });
    }
  },
  renderForm: function () {
    return <input 
              type='text'
              className='u-full-width'
              autoFocus
              defaultValue={this.props.value}
              onKeyPress={this.keyPress}
              onChange={this.valueHandler} />
  }, 
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className='six columns'>
              <span>email address:</span>
            </div>
            <div className="six columns">
              <a onClick={this.editHandler}>update</a>
            </div>
          </div>
          <div>
            <div className="six columns">
              {this.state.edit? this.renderForm(): this.props.value}
            </div>
            <div className="six columns">
              <a>primary</a>
            </div>
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

module.exports = Email;