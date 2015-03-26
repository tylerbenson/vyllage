var React = require('react');

var MoveBtn = React.createClass({

  render: function() {
    return (
      <button className='button cancel move'>
        <i className='icon ion-arrow-move'></i>
        Move
      </button>
    );
  }
});

module.exports = MoveBtn;
