var Reflux = require('reflux');

module.exports = Reflux.createActions([
  //
  'getDocumentId',
  'getResume',
  'getHeader',
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
  'toggleNav'
]);