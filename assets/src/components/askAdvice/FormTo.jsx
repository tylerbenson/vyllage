var React = require('react');
var RecipientEdit = require('./RecipientEdit');
var RecipientList = require('./RecipientList');
var Suggestions = require('./RecipientSuggestions');
var assign = require('lodash.assign');

var FormTo = React.createClass({
  render: function () {
    return (
        <div className=''>
          <RecipientList 
            recipients={this.props.recipients}
            selectedRecipient={this.props.selectedRecipient} />
          <div className="to content">
            <h2>To:</h2>
            <div className='form'>
              <RecipientEdit 
                recipient={this.props.recipient}
                recipients={this.props.recipients}
                suggestions={this.props.suggestions}
                selectedRecipient={this.props.selectedRecipient}
                selectedSuggestion={this.props.selectedSuggestion}
              />
              
              <Suggestions
                show={this.props.showSuggestions}
                suggestions={this.props.suggestions}
                selectedSuggestion={this.props.selectedSuggestion}
              />
            </div>
          </div>
        </div>
    );
  }
});


module.exports = FormTo;