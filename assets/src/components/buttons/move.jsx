var React = require('react');

var MoveBtn = React.createClass({
  render: function() {
    return (
      <a style={{textAlign: 'center'}} className="inverted secondary button small move" {...this.props}>
        <i className="ion-arrow-move"></i>
        Move
      </a>
    );
  }
});

module.exports = MoveBtn;
