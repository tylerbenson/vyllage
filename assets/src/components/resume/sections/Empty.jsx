var React = require('react');

var EmptySections = React.createClass({
  render: function () {
    return (
      <div className="empty-sections">
        <i className="ion-sad-outline"></i>
        <div>
          It looks empty in here.
          <strong>Add your first item!</strong>
        </div>
      </div>
    );
  }
});

module.exports = EmptySections;