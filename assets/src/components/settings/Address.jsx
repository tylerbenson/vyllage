var React = require('react');
var PrivacySelect = require('./PrivacySelect');
var ContentEditable = require("react-contenteditable");
var Address = React.createClass({
  getInitialState: function () {
    return { edit: false };
  },
  editHandler: function (e) {
    e.preventDefault();
    this.setState({edit: true});
  },
  valueHandler: function (e) {
    e.preventDefault();
    this.props.changeSetting('address', {value: e.target.value, privacy: this.props.privacy});
  },
  privacyHandler: function (e) {
    e.preventDefault();
    this.props.changeSetting('address', {value: this.props.value, privacy: e.target.value});
  },
  keyPress: function (e) {
    e.stopPropagation();
    if (e.key === 'Enter') {
      this.setState({edit: false});
    }
  },
  renderForm: function () {
    return <ContentEditable html={this.props.value} onChange={this.valueHandler} />;
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