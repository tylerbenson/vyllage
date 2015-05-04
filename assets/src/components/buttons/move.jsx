var React = require('react');

var MoveBtn = React.createClass({
  render: function() {
    return (
      <button class="inverted secondary small move">
        <i class="ion-arrow-move"></i>
          Move
      </button>
    );
  }
});

module.exports = MoveBtn;
