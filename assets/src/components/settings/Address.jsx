var React = require('react');
var PrivacySelect = require('./PrivacySelect');
var ContentEditable = require("react-contenteditable");
var Actions = require('./actions');

var Address = React.createClass({
  getInitialState: function () {
    return { edit: false };
  },
  editHandler: function (e) {
    e.preventDefault();
    this.setState({edit: !this.state.edit});
  },
  valueHandler: function (e) {
    e.preventDefault();
    Actions.changeSetting('address', {value: e.target.value, privacy: this.props.privacy});
  },
  privacyHandler: function (e) {
    e.preventDefault();
    Actions.changeSetting('address', {value: this.props.value, privacy: e.target.value});
  },
  keyPress: function (e) {
    e.stopPropagation();
    if (e.key === 'Enter') {
      this.setState({edit: false});
    }
  },
  renderForm: function () {
    return <div className='editable'><ContentEditable html={this.props.value} onChange={this.valueHandler} /></div>;
  }, 
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
          <div>
            <div className="six columns">
              address:
              {this.state.edit? this.renderForm(): <address dangerouslySetInnerHTML={{__html: this.props.value}}></address>}
            </div>
            <div className="six columns">
              <a onClick={this.editHandler}>update</a>
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

module.exports = Address;