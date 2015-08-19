var React = require('react');

var MoveBtn = React.createClass({
  render: function() {
    return (
     <span className="inverted secondary button small move move-sub" {...this.props}>
        <i className="ion-arrow-move"></i>
        Move
      </span>
    );
  }
});

module.exports = MoveBtn;
