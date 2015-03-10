var React = require('react');
var PrivacySelect = require('./PrivacySelect');
var Actions = require('./actions');

var OtherItem = React.createClass({
  getInitialState: function () {
    return { edit: false };
  },
  editHandler: function (e) {
    e.preventDefault();
    this.setState({edit: !this.state.edit});
  },
  valueHandler: function (e) {
    e.preventDefault();
    Actions.updateOther({value: e.target.value, privacy: this.props.privacy}, this.props.index);
  },
  privacyHandler: function (e) {
    e.preventDefault();
    Actions.updateOther({value: this.props.value, privacy: e.target.value}, this.props.index);
  },
  keyPress: function (e) {
    e.stopPropagation();
    if (e.key === 'Enter') {
      this.setState({edit: false});
    }
  },
  removeHandler: function (e) {
    e.preventDefault();
    Actions.removeOther(this.props.index);
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
              {this.state.edit ? this.renderForm(): this.props.value}
            </div>
            <div className="six columns">
              <div className={this.state.edit ? "six columns": ""}>
                <a onClick={this.editHandler}>{this.state.edit? 'update': 'update/remove'}</a>
              </div>
              {this.state.edit ? <div className="six columns">
                {<a onClick={this.removeHandler}>remove</a>}
              </div>: null}
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

module.exports = OtherItem;