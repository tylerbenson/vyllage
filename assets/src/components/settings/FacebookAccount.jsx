var React = require('react');
var Actions = require('./actions');

var FacebookAccount = React.createClass({
  getInitialState: function () {
    return {confirm: false}
  },
  removeHandler: function (e) {
    e.preventDefault();
    if (this.state.confirm) {
      Actions.changeSetting('facebookAccount', false);
      this.setState({
        confirm: false
      })
    } else {
      this.setState({
        confirm: true
      })
    }
  },
  cancelHandler: function (e) {
    e.preventDefault();
    this.setState({confirm: false});
  },
  renderValue: function () {
    return <div>facebook account: {this.props.value ? 'linked': <a>login</a>}</div>;
  },
  renderConfirm: function () {
    return <div>facebook account: <span className='error'>Are you sure ?</span></div>;
  },
  render: function () {
    return (
      <li className="row settings-profile-item">
        <div className={this.state.confirm? "eight columns": "nine columns"}>
          {this.state.confirm ? this.renderConfirm(): this.renderValue()}
        </div>
        <div className={this.state.confirm? "four columns margin-fix": "three columns"}>
          {this.props.value ? <a className="" onClick={this.removeHandler}>remove</a>: null}
          {this.state.confirm ? <span> or </span>: null}
          {this.state.confirm ? <a className="" onClick={this.cancelHandler}>cancel</a>: null}
        </div>
      </li>
    );
  }
});

module.exports = FacebookAccount;