var Reflux = require('reflux');

module.exports = Reflux.createActions([
  //
  'getResume',
  // sections
  'getSections',
  'getSection',
  'postSection',
  'putSection',
  'deleteSection',
  // comments
  'getComments',
  'getComment',
  'postComment'
]);