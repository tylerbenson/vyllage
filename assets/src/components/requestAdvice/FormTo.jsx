var React = require('react');
var RecipientEdit = require('./RecipientEdit');
var RecipientList = require('./RecipientList');
var Suggestions = require('./RecipientSuggestions');
var assign = require('lodash.assign');

var FormTo = React.createClass({
  render: function () {
    return (
        <div className='request-advice-form-to row'>
          <div className="one column" style={{paddingTop: '16px', textAlign: 'center'}}>
            to:
          </div>
          <div className='nine columns'>
            <RecipientEdit 
              recipient={this.props.recipient}
              recipients={this.props.recipients}
              suggestions={this.props.suggestions}
              selectedRecipient={this.props.selectedRecipient}
              selectedSuggestion={this.props.selectedSuggestion}
            />
            <RecipientList 
              recipients={this.props.recipients}
              selectedRecipient={this.props.selectedRecipient} />
            <Suggestions
              show={this.props.showSuggestions}
              suggestions={this.props.suggestions}
              selectedSuggestion={this.props.selectedSuggestion}
            />
          </div>
        </div>
    );
  }
});


module.exports = FormTo;