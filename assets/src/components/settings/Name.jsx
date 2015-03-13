var React = require('react');
var Actions = require('./actions');

var Name = React.createClass({
  getInitialState: function () {
    return { edit: false };
  },
  editHandler: function (e) {
    e.preventDefault();
    this.setState({edit: !this.state.edit});
  },
  changeHandler: function (e) {
    e.preventDefault();
    Actions.changeSetting('name', e.target.value);
  },
  keyPress: function (e) {
    if (e.key === 'Enter') {
      this.setState({edit: false});
    }
  },
  renderName: function () {
    return (
      <div className='row'>
        <h5>{this.props.value}</h5>
        <a onClick={this.editHandler}>change</a>
      </div>
    );
  },
  renderForm: function () {
    return (
      <div className='row'>
        <input 
          type='text'
          autoFocus={true}
          defaultValue={this.props.value}
          onChange={this.changeHandler}
          onKeyPress={this.keyPress} />
        <a onClick={this.editHandler}>update</a>
      </div>
    );
  },
  render: function () {
    return (
      <div className="settings-name">
        {this.state.edit ? this.renderForm(): this.renderName()}
        <h6>Member since: Jan 1, 2014</h6>
      </div>
    );
  }
});

module.exports = Name;