var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
var urlTemplate = require('url-template');
var validator = require('validator');

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
  'toggleSorting',
  'togglePrintModal'
]);

  EditorActions.getAllSections.preEmit = function(){

    var documentId;
    var tempDocumentId = window.localStorage.getItem('ownDocumentId');
    if( tempDocumentId != undefined && validator.isNumeric(tempDocumentId ) ){
      documentId = tempDocumentId;
    } 

    if( documentId !== undefined ){
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
  }

  module.exports = EditorActions;