var React = require('react');
var RecipientEdit = require('./RecipientEdit');
var RecipientList = require('./RecipientList');
var Suggestions = require('./RecipientSuggestions');
var assign = require('lodash.assign');

var FormTo = React.createClass({
  render: function () {
    return (
      <div className="to content">
        <h2>To:</h2>
        <RecipientEdit
          recipient={this.props.recipient}
          recipients={this.props.recipients}
          suggestions={this.props.suggestions}
          selectedRecipient={this.props.selectedRecipient}
          selectedSuggestion={this.props.selectedSuggestion}
        />
        <Suggestions
          show={this.props.showSuggestions}
          position={this.props.suggestionPosition}
          suggestions={this.props.suggestions}
          selectedSuggestion={this.props.selectedSuggestion}
        />
      </div>
    );
  }
});


module.exports = FormTo;