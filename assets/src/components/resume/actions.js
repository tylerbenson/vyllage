var Reflux = require('reflux');

module.exports = Reflux.createActions([
  //
  'getResume',
  'getHeader',
  'updateTagline',
  // sections
  'getSections',
  'postSection',
  'putSection',
  'deleteSection',
  // comments
  'getComments',
  'postComment',
  // Ui
  'enableEditMode',
  'disableEditMode',
  'showComments',
  'hideComments',
  'toggleComments'
]);