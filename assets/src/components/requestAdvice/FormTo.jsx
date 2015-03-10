var React = require('react');
var RecipientEdit = require('./RecipientEdit');
var RecipientList = require('./RecipientList');
var Suggestions = require('./RecipientSuggestions');
var assign = require('lodash.assign');
var Reflux =require('reflux');
var requestAdviceStore = require('./store');

var FormTo = React.createClass({
  mixins: [Reflux.connect(requestAdviceStore)],
  render: function () {
    return (
        <div className='request-advice-form-to row'>
          <div className="one column rqst-key-word" style={{paddingTop: '16px'}}>
            to:
          </div>
          <div className='nine columns'>
            <RecipientEdit 
              recipient={this.state.recipient}
              recipients={this.state.recipients}
              suggestions={this.state.suggestions}
              selectedRecipient={this.state.selectedRecipient}
              selectedSuggestion={this.state.selectedSuggestion}
            />
            <RecipientList 
              recipients={this.state.recipients}
              selectedRecipient={this.state.selectedRecipient} />
            <Suggestions
              show={this.state.showSuggestions}
              suggestions={this.state.suggestions}
              selectedSuggestion={this.state.selectedSuggestion}
            />
          </div>
          <div className="two columns fb-button">
            <span className="small-text">ask your</span><br/>
            <span className="big-text">facebook</span><br/>
            <span className="small-text">friends</span><br/>
          </div>
        </div>
    );
  }
});


module.exports = FormTo;