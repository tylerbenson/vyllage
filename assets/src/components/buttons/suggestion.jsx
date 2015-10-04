var React = require('react');

var SuggestionButton = React.createClass({
  render: function() {
    return (
      <button tabIndex={-1} className='small' onClick={this.props.onClick}>
        <i className="icon ion-checkmark"></i>
        Submit
      </button>
    );
  }
});

module.exports = SuggestionButton;


