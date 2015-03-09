var Reflux = require('reflux');

module.exports = Reflux.createActions([
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