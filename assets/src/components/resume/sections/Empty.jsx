var React = require('react');

var EmptySections = React.createClass({
  render: function () {
    var contents = {
        title: "Add your first item now!",
        message: "It looks empty in here.",
        icon: "ion-android-document"
      };

    switch(this.props.status){
      case "403":
        contents = {
          title: "Access Forbidden",
          message: "This resumé is private.",
          icon: "ion-android-lock"
        };
        break;
      default:
    };

    return (
      <div className="empty-sections">
        <i className={contents.icon}></i>
        <div>
          <div>{contents.message}</div>
          <strong>{contents.title}</strong>
        </div>
      </div>
    );
  }
});

module.exports = EmptySections;