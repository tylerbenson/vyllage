var React = require('react');
var Actions = require('./actions');

var Organization = React.createClass({
  getInitialState: function () {
    return { edit: false };
  },
  toggleHandler: function (e) {
    e.preventDefault();
    this.setState({edit: !this.state.edit});
  },
  changeHandler: function (e) {
    e.preventDefault();
    Actions.changeSetting('organization', e.target.value);
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
              onChange={this.changeHandler} />
  },
  render: function () {
    return (
      <li className="row settings-profile-item">
        <div className='nine columns'>
          <div className='six columns'>
            organization: 
          </div>
          <div className='six columns'>
            {this.state.edit ? this.renderForm(): this.props.value}
          </div>
        </div>
        <div className='three columns'>
          <a className="" onClick={this.toggleHandler}>{this.state.edit ? 'update' : 'change'}</a>
        </div>
      </li>
    );
  }
});

module.exports = Organization;