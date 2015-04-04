var Reflux = require('reflux');

module.exports = Reflux.createActions([
  'postAskAdvice',
  'getSuggestions',
  'changeRecipient',
  'addRecipient',
  'updateRecipient',
  'removeRecipient',
  'selectRecipient',
  'selectSuggestion',
  'selectRecentSuggestion',
  'selectRecommendedSuggestion',
  'suggestionIndex',
  'closeSuggestions',
  'openSuggestions',
  'updateSubject',
  'updateMessage'
]);