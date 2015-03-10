var React = require('react');
var PrivacySelect = require('./PrivacySelect');
var Actions = require('./actions');
var validator = require("validator");

var Other = React.createClass({
  getInitialState: function () {
    return { 
      edit: false,
      error: false,
      value: '',
      privacy: 'organization'
    };
  },
  editHandler: function () {
    this.setState({edit: true});
  },
  valueHandler: function (e) {
    e.preventDefault();
    this.setState({value: e.target.value});
  },
  privacyHandler: function (e) {
    e.preventDefault();
    this.setState({privacy: e.target.value});
  },
  keyPress: function (e) {
    e.stopPropagation();
    if (e.key === 'Enter') {
      if (validator.isURL(this.state.value)) {
        Actions.addOther({value: this.state.value, privacy: this.state.privacy})
        this.setState({
          edit: false,
          error: false,
          value: ""
        });
      } else {
        this.setState({error: true});
      }
    }
  },
  renderForm: function () {
    return (
      <div className="">
        <input
          type='text'
          value={this.state.value}
          autoFocus={true}
          onKeyPress={this.keyPress}
          onChange={this.valueHandler} 
        />
        {this.state.error ? <p className='error'>* Not a valid link</p>: null}
      </div>
    );
  },
  render: function () {
    return (
      <li className="row settings-account-item">
        <div className="eight columns">
            <div className="six columns">
              other
            </div>
            <div className="six columns">
              <a onClick={this.editHandler}>add</a>
            </div>
        </div>
        <div className='four columns'>
          <PrivacySelect
            value={this.state.privacy} 
            organization={this.props.organization}
            onChange={this.privacyHandler} />
        </div>
        { this.state.edit ? this.renderForm(): null }
      </li>
    );
  }
});

module.exports = Other;