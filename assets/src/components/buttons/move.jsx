var React = require('react');

var MoveBtn = React.createClass({
  render: function() {
    return (
      <button className="inverted secondary small move" {...this.props}>
        <i className="ion-arrow-move"></i>
        Move
      </button>
    );
  }
});

module.exports = MoveBtn;
