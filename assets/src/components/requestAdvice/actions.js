var Reflux = require('reflux');

module.exports = Reflux.createActions([
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
  'openSuggestions'
]);