var Reflux = require('reflux');

module.exports = Reflux.createActions([
  'postFeedback',
  'getSuggestions',
  'changeRecipient',
  'addRecipient',
  'updateRecipient',
  'removeUserRecipient',
  'removeNotRegisteredUserRecipient',
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