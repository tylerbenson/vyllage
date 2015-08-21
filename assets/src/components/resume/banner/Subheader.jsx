var React = require('react');
var Avatar = require('../../avatar');
var AddSection = require('../AddSection');

var Subheader = React.createClass({
  shouldComponentUpdate: function(nextProps, nextState) {
    return nextProps !== this.props || nextState !== this.state;
  },
  render: function () {
  var name = this.props.name || '';

    return (
      <section className="subheader">
        <div className="content">
          <div className="avatar-container">
            <Avatar src={this.props.avatar} size="32" />
          </div>
          <div className="info">
            <div className="name">{name}</div>
            <button className="flat small" onClick={this.props.onEditProfile}>
            	<i className="ion-edit"></i>
            	<span>Edit Profile</span>
            </button>
          </div>

          <AddSection sections={this.props.sections} />
        </div>
      </section>
    );
  }
});

module.exports = Subheader;