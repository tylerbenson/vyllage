var React = require('react');
var Actions = require('./actions');

var Role = React.createClass({
  getInitialState: function () {
    return { edit: false };
  },
  toggleHandler: function (e) {
    e.preventDefault();
    this.setState({edit: !this.state.edit});
  },
  changeHandler: function (e) {
    e.preventDefault();
    Actions.changeSetting('role', e.target.value);
    this.setState({edit: false});
  },
  renderForm: function () {
    return (
      <select onChange={this.changeHandler} value={this.props.value}>
        <option value='former student'>former student</option>
        <option value='student'>student</option>
        <option value='alumni'>alumni</option>
        <option value='prospective student'>prospective student</option>
      </select>
    );
  },
  render: function () {
    return (
      <li className="row settings-profile-item">
        <div className='nine columns'>
          role: {this.state.edit ? this.renderForm(): this.props.value}
        </div>
        <div className='three columns'>
          <a className="" onClick={this.toggleHandler}>{this.state.edit ? 'update' : 'change'}</a>
        </div>
      </li>
    );
  }
});

module.exports = Role;