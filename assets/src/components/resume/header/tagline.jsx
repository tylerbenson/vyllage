var React = require('react');

var TaglineContainer = React.createClass({
  render: function() {
    return (
      <div className="headline-container main">
        <div className="paragraph">
          <p className="tagline">{this.props.profileData.tagline}</p>
        </div>
      </div>
    );
  }
});

module.exports = TaglineContainer;