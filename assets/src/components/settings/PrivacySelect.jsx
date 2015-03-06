var React = require('react');

var PrivacySelect = React.createClass({
  render: function () {
    return (
      <select value={this.props.value} className="u-full-width" onChange={this.props.onChange}>
        <option value="everyone">everyone</option>
        <option value="none">no one</option>
        <option value="organization">{this.props.organization}</option>
      </select>
    );
  }
});

module.exports = PrivacySelect;