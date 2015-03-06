var React = require('react');

var SharedLinks = React.createClass({
  getInitialState: function () {
    return {confirm: false}
  },
  removeHandler: function (e) {
    e.preventDefault();
    if (this.state.confirm) {
      this.props.changeSetting('sharedLinks', false);
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
  render: function () {
    return (
       <li className="row settings-profile-item">
          <div className={this.state.confirm? "seven columns": "nine columns"}>
            <span>shared links</span>
            {this.state.confirm? <span className='error'>Are you sure?</span>: null}
          </div>
          <div className={this.state.confirm? "five columns": "three columns"}>
            <a className="" onClick={this.removeHandler}>reset all</a>
            {this.state.confirm ? <span> or </span>: null}
            {this.state.confirm ? <a className="" onClick={this.cancelHandler}>cancel</a>: null}
          </div>
        </li>
    );
  }
});

module.exports = SharedLinks;