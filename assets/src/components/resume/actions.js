var Reflux = require('reflux');

module.exports = Reflux.createActions([
  //
  'getResume',
  'getHeader',
  'getDocumentId',
  'updateTagline',
  // sections
  'getSections',
  'postSection',
  'putSection',
  'deleteSection',
  'updateSectionOrder',
  'moveSection',
  // comments
  'getComments',
  'postComment',
  // Ui
  'enableEditMode',
  'disableEditMode',
  'showComments',
  'hideComments',
  'toggleComments',
  'toggleNav',
  'togglePrintModal'
]);