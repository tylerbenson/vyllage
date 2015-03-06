var React = require('react');

var PrivacySelect = React.createClass({
  render: function () {
    return (
      <select className="u-full-width">
        <option value="everyone">everyone</option>
        <option value="none">no one</option>
        <option value="organization">{this.props.organization}</option>
      </select>
    );
  }
});

module.exports = PrivacySelect;