var React = require('react');

var LoadingSection = React.createClass({
  render: function () {
    return (
      <div className="empty-sections">
        <i className="ion-load-d"></i>
        <div>
          Loading..
          <strong>Please wait.</strong>
        </div>
      </div>
    );
  }
});

module.exports = LoadingSection;