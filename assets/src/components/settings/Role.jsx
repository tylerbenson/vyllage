var React = require('react');

var Role = React.createClass({
  getInitialState: function () {
    return { edit: false };
  },
  editHandler: function (e) {
    e.preventDefault();
    this.setState({edit: true});
  },
  changeHandler: function (e) {
    e.preventDefault();
    this.props.changeSetting('role', e.target.value);
    this.setState({edit: false});
  },
  // keyPress: function (e) {
  //   e.preventDefault();
  //   if (e.key === 'Enter') {
  //     this.setState({edit: false});
  //   }
  // },
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
          <a className="" onClick={this.editHandler}>change</a>
        </div>
      </li>
    );
  }
});

module.exports = Role;