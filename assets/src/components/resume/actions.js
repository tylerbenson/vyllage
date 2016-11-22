var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
var urlTemplate = require('url-template');
var validator = require('validator');
var omit = require('lodash.omit');
var assign = require('lodash.assign');

 var EditorActions = Reflux.createActions([
  //
  'getResume',
  'getHeader',
  'getDocumentId',
  'publishDocumentId',
  'updateTagline',
  'publishTagLine',
  // sections
  'getAllSections',
  'publishSections',
  'getSections',
  'postSection',
  'postNewSection',
  'publishNewSection',
  'putSection',
  'publishSection',
  'deleteSection',
  'publishDeleteSection',
  'deleteNewSection',
  'moveGroupOrder',
  'moveSectionOrder',
  'postSectionOrder',
  // comments
  'getComments',
  'publishComments',
  'postComment',
  'publishComment',
  'deleteComment',
  'publishDeleteComment',
  // advice
  'getAdvices',
  'publishAdvices',
  'saveSectionAdvice',
  'publishSectionAdvice',
  'deleteAdvice',
  'publishDeleteAdvice',
  'mergeAdvice',
  'publishMergeAdvice',
  'toggleEdits',
  'editAdvice',
  'publishEditedAdvices',
  'putSectionForAdvice',
  'publishSectionForAdvice',
  // Ui
  'enableEditMode',
  'disableEditMode',
  'showComments',
  'hideComments',
  'setStatus',
  'toggleComments',
  'toggleNav',
  'toggleSorting',
  'togglePrintModal'
]);


  var documentId = window.location.pathname.split('/')[2];
  if(!validator.isNumeric(documentId)){
    var tempDocumentId = window.localStorage.getItem('ownDocumentId');
    //Load own resumé
    if(tempDocumentId != undefined && validator.isNumeric(tempDocumentId)){
      documentId = tempDocumentId;
    }
  }

  var metaHeader = document.getElementById('meta_header');
  if (metaHeader) {
    var tokenHeader = metaHeader.content;
  }
  var metaToken = document.getElementById('meta_token');
  if (metaToken) {
    var tokenValue = metaToken.content;
  }



  // Get document id
  EditorActions.getDocumentId.preEmit = function(){

    var url = urlTemplate
              .parse(endpoints.documentId)
              .expand({documentType: 'RESUME'});
    request
      .get(url)
      .end(function (err, res) {
        if(res) {
          if(res.ok && res.body.RESUME.length > 0) {
            EditorActions.publishDocumentId(res.body.RESUME[0]);
          }
        }
      });
  }


  // Update tagline
  EditorActions.updateTagline.preEmit = function( tagline ){

    var url = urlTemplate
                .parse(endpoints.resumeHeader)
                .expand({documentId: documentId});
    request
      .put(url)
      .set( tokenHeader, tokenValue )
      .send({tagline: tagline})
      .end(function (err, res) {
        EditorActions.publishTagLine(tagline);
      });
  }


  // Get all sections with header
  EditorActions.getAllSections.preEmit = function(){
    var header;
    var headerUrl = urlTemplate
                .parse(endpoints.resumeHeader)
                .expand({documentId: documentId});
    request
      .get(headerUrl)
      .set('Accept', 'application/json')
      .end(function (err, res) {
        EditorActions.setStatus(res.status);
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



  // Get Comments
  EditorActions.getComments.preEmit = function( sectionId ){
    var url = urlTemplate
                .parse(endpoints.resumeComments)
                .expand({
                  documentId: documentId,
                  sectionId: sectionId
                });
    request
      .get(url)
      .end(function (err, res) {
        if( res.body )
          EditorActions.publishComments( res.body , sectionId);
      }.bind(this));
  }


  // Get Advices
  EditorActions.getAdvices.preEmit = function( sectionId ){
    request
      .get('/resume/'+documentId+'/section/'+sectionId+'/advice')
      .set('Accept', 'application/json')
      .end(function (err, res) {
        if( res.body )
          EditorActions.publishAdvices(res.body , sectionId );
      }.bind(this));
  }



  // section edit
  EditorActions.putSection.preEmit = function( data ){
    if(data.newSection) {
      EditorActions.postNewSection( data );
    }
    else {
      var url = urlTemplate
                .parse(endpoints.resumeSection)
                .expand({
                  documentId: documentId,
                  sectionId: data.sectionId
                });

      request
        .put(url)
        .set(tokenHeader, tokenValue)
        .send(omit(data, ['uiEditMode', 'showComments', 'comments', 'newSection', 'isSupported','advices','showEdits']))
        .end(function (err, res) {
          if(res.body) {
            EditorActions.publishSection( data , res.body );
          }
        });
    }
  }


  EditorActions.postNewSection.preEmit = function(data){
    var url = urlTemplate
                .parse(endpoints.resumeSections)
                .expand({
                  documentId: documentId,
                });
    request
      .post(url)
      .set(tokenHeader,tokenValue)
      .send(omit(data, ['uiEditMode', 'showComments', 'comments', 'newSection', 'isSupported','advices','showEdits']))
      .end(function (err, res) {
        if(res.body)
        EditorActions.publishNewSection(res.body);
      });
  }


  EditorActions.deleteSection.preEmit = function(sectionId){
    var url = urlTemplate
                .parse(endpoints.resumeSection)
                .expand({
                  documentId: documentId,
                  sectionId: sectionId
                });
    request
      .del(url)
      .set(tokenHeader, tokenValue)
      .set('Content-Type', 'application/json')
      .send({documentId: documentId, sectionId: sectionId})
      .end(function (err, res) {
        EditorActions.publishDeleteSection(sectionId);
      });
  }


  EditorActions.postSectionOrder.preEmit =  function( sections ) {
    var order = sections.map(function (section) {
      return section.sectionId;
    });

    var url = urlTemplate
                .parse(endpoints.resumeSectionOrder)
                .expand({documentId: documentId});
    request
      .put(url)
      .set(tokenHeader,tokenValue)
      .send(order)
      .end(function (err, res){});
  }



  EditorActions.postComment.preEmit = function( data ){

    var url = urlTemplate
                .parse(endpoints.resumeComments)
                .expand({
                  documentId: documentId,
                  sectionId: data.sectionId,

                });
    data = assign({}, data ,{ sectionVersion : 1})
    request
      .post(url)
      .set( tokenHeader, tokenValue)
      .send(data)
      .end(function (err, res) {
        if(res.body){
          EditorActions.publishComment(res.body , data);
        }
      });
  }

  EditorActions.deleteComment.preEmit = function( comment , sectionId ){
    var url = urlTemplate
                .parse(endpoints.resumeComment)
                .expand({
                  documentId: documentId,
                  sectionId: sectionId,
                  commentId: comment.commentId
                });
    request
      .del(url)
      .set(tokenHeader,tokenValue)
      .set('Content-Type', 'application/json')
      .send(comment)
      .end(function (err, res) {
         if(res.status == 202){
          EditorActions.publishDeleteComment( comment , sectionId );
         }
      });

  }

  EditorActions.saveSectionAdvice.preEmit = function(section){
    request
      .post('/resume/'+documentId+'/section/'+section.sectionId+'/advice')
      .set(tokenHeader,tokenValue)
      .send({
        sectionId : section.sectionId,
        sectionVersion : section.sectionVersion,
        userId : document.getElementById('meta_userInfo_user').content,
        documentSection : omit(section, ['uiEditMode', 'showComments', 'comments', 'newSection', 'isSupported','advices','numberOfAdvices','showEdits'])
      })
      .end(function(err,res){
        if(res.status == 200){
          EditorActions.publishSectionAdvice(section , res.body)
        }
      });
  }


  EditorActions.deleteAdvice.preEmit = function( advice , section){
    request
      .put('/resume/'+documentId+'/section/'+section.sectionId+'/advice/'+advice.sectionAdviceId)
      .set( tokenHeader, tokenValue)
      .send(advice)
      .end(function (err, res) {
        if( res.status == 200){
       //   EditorActions.publishDeleteAdvice(section , res.body);
        }
      });
  }


  EditorActions.putSectionForAdvice.preEmit = function( data , section , advice ){
    var url = urlTemplate
                .parse(endpoints.resumeSection)
                .expand({
                  documentId: documentId,
                  sectionId: data.sectionId
                });
    request
      .put(url)
      .set( tokenHeader,tokenValue)
      .send(omit(data, ['uiEditMode', 'showComments', 'comments', 'newSection', 'isSupported','advices','showEdits']))
      .end(function (err, res) {

        EditorActions.publishSectionForAdvice(res.body , section , advice );

      });
  }

  EditorActions.mergeAdvice.preEmit = function(advice , section){
    request
      .put('/resume/'+documentId+'/section/'+section.sectionId+'/advice/'+advice.sectionAdviceId)
      .set(tokenHeader,tokenValue)
      .send(advice)
      .end(function (err, res) {
        if( res.status == 200){
          EditorActions.publishMergeAdvice( res.body.documentSection , advice , section );
        }
      });
  }



  EditorActions.editAdvice.preEmit = function( advice , section){
    request
      .put('/resume/'+documentId+'/section/'+section.sectionId+'/advice/'+advice.sectionAdviceId)
      .set( tokenHeader, tokenValue)
      .send(advice)
      .end(function (err, res) {
        if( res.status == 200){
          if(res.body){
            //EditorActions.publishEditedAdvices(res.body);
          }
        }
      });
  } 


  module.exports = EditorActions;