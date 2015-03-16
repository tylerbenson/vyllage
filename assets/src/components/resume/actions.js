var Reflux = require('reflux');

module.exports = Reflux.createActions([
  //
  'getResume',
  // sections
  'getSections',
  'postSection',
  'putSection',
  'deleteSection',
  // comments
  'getComments',
  'postComment'
]);