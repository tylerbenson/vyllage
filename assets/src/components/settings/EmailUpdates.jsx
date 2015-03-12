var React = require('react');
var Actions = require('./actions');

var EmailUpdates = React.createClass({
  getInitialState: function () {
    return { edit: false };
  },
  toggleHandler: function (e) {
    e.preventDefault();
    this.setState({edit: !this.state.edit});
  },
  changeHandler: function (e) {
    e.preventDefault();
    Actions.changeSetting('emailUpdates', e.target.value);
    this.setState({edit: false});
  },
  renderForm: function () {
    return (
      <select onChange={this.changeHandler} value={this.props.value}>
        <option value='weekly'>weekly</option>
        <option value='bi-weekly'>bi-weekly</option>
        <option value='monthly'>monthly</option>
        <option value='no-updates'>no updates</option>
      </select>
    );
  },
  render: function () {
    return (
      <li className="row settings-profile-item">
        <div className='nine columns'>
          email updates: {this.state.edit ? this.renderForm(): this.props.value}
        </div>
        <div className='three columns'>
          <a className="" onClick={this.toggleHandler}>{this.state.edit ? 'update' : 'change'}</a>
        </div>
      </li>
    );
  }
});

module.exports = EmailUpdates;