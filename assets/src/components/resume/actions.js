var Reflux = require('reflux');

module.exports = Reflux.createActions([
  //
  'getResume',
  'getHeader',
  'putHeader',
  // sections
  'getSections',
  'postSection',
  'putSection',
  'deleteSection',
  // comments
  'getComments',
  'postComment'
]);