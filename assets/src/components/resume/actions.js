var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
var urlTemplate = require('url-template');

 var EditorActions = Reflux.createActions([
  //
  'getResume',
  'getHeader',
  'getDocumentId',
  'updateTagline',
  // sections
  'getAllSections',
  'publishSections',
  'getSections',
  'postSection',
  'putSection',
  'deleteSection',
  'deleteNewSection',
  'moveGroupOrder',
  'moveSectionOrder',
  // comments
  'getComments',
  'postComment',
  'deleteComment',
  // advice 
  'saveSectionAdvice',
  'deleteAdvice',
  'mergeAdvice',
  'toggleEdits',
  // Ui
  'enableEditMode',
  'disableEditMode',
  'showComments',
  'hideComments',
  'toggleComments',
  'toggleNav',
  'togglePrintModal'
]);

  EditorActions.getAllSections.preEmit = function(){
    var documentId = window.location.pathname.split('/')[2];
    var header;

    var headerUrl = urlTemplate
                .parse(endpoints.resumeHeader)
                .expand({documentId: documentId});
    request
      .get(headerUrl)
      .set('Accept', 'application/json')
      .end(function (err, res) {
        if (res.ok) {
          // needed to make multi ajax
          header = res.body;
          var url = urlTemplate
                      .parse(endpoints.resumeSections)
                      .expand({documentId: documentId });
          request
            .get(url)
            .set('Accept', 'application/json')
            .end(function (err, res) {
              if (res.ok) {
                EditorActions.publishSections( res.body , header );
              }
            });
        }

      });

  }

  module.exports = EditorActions;