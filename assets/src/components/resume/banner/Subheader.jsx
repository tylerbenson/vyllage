var React = require('react');

var Subheader = React.createClass({
  render: function () {
    return (
      <section className="subheader">
        <div className="content">
          <div className="name">{this.props.name}</div>
          <button className="flat small" onClick={this.props.onEditProfile}>
          	<i className="ion-edit"></i>
          	<span>Edit Profile</span>
          </button>
        </div>
      </section>
    );
  }
});

module.exports = Subheader;