var React = require('react');

var Name = React.createClass({
  getInitialState: function () {
    return { edit: false };
  },
  editHandler: function (e) {
    e.preventDefault();
    this.setState({edit: true});
  },
  changeHandler: function (e) {
    console.log(e.target.value);
    e.preventDefault();
    this.props.changeSetting('name', e.target.value);
  },
  keyPress: function (e) {
    e.preventDefault();
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
      <input 
        type='text'
        autoFocus
        value={this.props.value}
        onChange={this.changeHandler}
        onKeyPress={this.keyPress} />
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