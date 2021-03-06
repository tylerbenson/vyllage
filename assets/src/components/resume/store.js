var React = require('react');
var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
var sections = require('../sections');
var urlTemplate = require('url-template');
var where = require('lodash.where');
var findindex = require('lodash.findindex');
var omit = require('lodash.omit');
var assign = require('lodash.assign');
var max = require('lodash.max');
var update = require('react/lib/update');
var sortby = require('lodash.sortby');
var filter = require('lodash.filter');
var PubSub = require('pubsub-js');
var clone = require('clone');
var validator = require('validator');
var EditorActions = require('./actions');

module.exports = Reflux.createStore({
  listenables: EditorActions,
  init: function () {
    var metaHeader = document.getElementById('meta_header');
    if (metaHeader) {
      this.tokenHeader = metaHeader.content;
    }
    var metaToken = document.getElementById('meta_token');
    if (metaToken) {
      this.tokenValue = metaToken.content;
    }
    this.documentId = window.location.pathname.split('/')[2];
    EditorActions.getDocumentId();
    var tempDocumentId = window.localStorage.getItem('ownDocumentId');
    if(tempDocumentId !== undefined && validator.isNumeric(tempDocumentId)){
      this.ownDocumentId = tempDocumentId;
    }
    this.resume = {
      status: null,
      ownDocumentId: this.ownDocumentId !== undefined ? this.ownDocumentId : this.documentId,
      documentId: this.documentId,
      header: {
        firstName: '',
        middleName: '',
        lastName: ''
      },
      sections: [],
      all_section:[],
      sectionOrder: ['summary', 'experience', 'education', 'skills'],
      isNavOpen: false,
      isPrintModalOpen: false,
      isSorting: false
    };
  },

  /*Notifications*/
  remindToShare: function() {
    var message = <span>Your resumé has been updated!  Now, share  it to the world to <a href="/resume/get-feedback">get feedback.</a></span>;
    var timeout = 6000;

    PubSub.publish('banner-alert', {isOpen: true, message: message, timeout: timeout});
  },
  notifyEditSubmission: function() {
    var message = <span>Well done! We will notify {this.resume.header.firstName} of your edit suggestion.</span>;
    var timeout = 6000;

    PubSub.publish('banner-alert', {isOpen: true, message: message, timeout: timeout});
  },
  notifyEditMerge: function() {
    var message = <span>Edit suggestion applied.</span>;
    var timeout = 4000;

    PubSub.publish('banner-alert', {isOpen: true, message: message, timeout: timeout});
  },
  notifyError: function(error) {
	  var message = <span>  {error} </span>;
	  var timeout = 8000;

	  PubSub.publish('banner-alert', {isOpen: true, message: message, timeout: timeout}); 
  },
  /*End of Notifications*/

  /* Helper functions */
  getMaxSectionPostion: function () {
    var section = max(this.resume.sections, 'sectionPosition');
    // Return 0 if sections is empty
    return (section !== -Infinity) ? section.sectionPosition: 0;
  },

  isSupportedSection: function (type) {
    var supported = ['SummarySection','JobExperienceSection','EducationSection','SkillsSection','CareerInterestsSection','ProjectsSection'];
    return supported.indexOf(type) > -1;
  },

  doProcessSection: function(sections , owner ){
    var tmp_section = [];
    var all_section = clone( sections );
    all_section = sortby(all_section ,'sectionPosition');
    if( all_section.length > 0 ){
      all_section.forEach(function(section){
          section.isSupported = this.isSupportedSection(section.type);
          if( section.isSupported){
              if( tmp_section.length){
                var findIt = findindex(tmp_section, {'type': section.type});
                 if( findIt == -1){
                  var tmp_data = {
                      type: section.type,
                      title : section.title,
                      id : section.sectionId,
                      owner : owner,
                      child : []
                  }
                  tmp_data.child = where( all_section, { 'type': section.type });
                  tmp_section.push(tmp_data);
                 }
              }else{
                var tmp_data ={
                  type: section.type,
                  title : section.title,
                  id : section.sectionId,
                  owner : owner,
                  child : []
                };
                tmp_data.child = where( all_section, { 'type': section.type });
                tmp_section.push(tmp_data);
              }
          }
      }.bind(this));
      if(tmp_section.length > 0 ) return tmp_section; else [];
    }else{
      return [];
    }
  },

  makeSectionsLinear: function(all_section){
    var linear_section = [];
    all_section.map(function(group , index ){
          if( group.child.length ){
            group.child.map(function(section , section_index){
               section.sectionPosition = linear_section.length;
               linear_section.push(section);
            });
          }
      });
      this.resume.sections.map(function(section,index){
          var exist = filter( linear_section ,{ 'sectionId': section.sectionId });
          if( !exist.length ){
            section.sectionPosition = linear_section.length;
            linear_section.push(section);
          }
      });
      this.update();
      EditorActions.postSectionOrder( linear_section );
  },
  /* End of Helper functions */
 
  onPublishDocumentId: function(id) {
    this.resume.ownDocumentId = id;
    this.resume.documentId = isNaN(parseInt(this.resume.documentId)) ? this.resume.ownDocumentId : this.resume.documentId;
    this.documentId = this.resume.documentId;
    window.localStorage.setItem('ownDocumentId', this.resume.ownDocumentId);
    this.trigger(this.resume);
  },

  onPublishTagLine: function (tagline) {
    this.resume.header.tagline = tagline;
    this.update();
  },

  // Get All the sections 
  onPublishSections : function( sections , header ){
    var self = this;
      this.resume.header = header;
      if( this.resume.sections.length > 0 ){
        this.resume.sections = this.resume.sections.concat(sections);
      }else{
        this.resume.sections = sections;
      }
      this.resume.sections.forEach(function (section) {
        section.isSupported = this.isSupportedSection(section.type);
        if( section.sectionId ){
          if (section.numberOfComments > 0) {
            EditorActions.getComments( section.sectionId);
          }
          EditorActions.getAdvices( section.sectionId );
        }
      }.bind(this));
      this.update();
  },
  // Get only single section
  onPublishNewSection: function( newSection ){
    var section = assign({}, newSection);
    section.isSupported = this.isSupportedSection(section.type);
    var tempSectionIndex = findindex( this.resume.sections , { newSection : true } );
    this.resume.sections.splice( tempSectionIndex , 1);
    this.resume.sections.push(section);
    this.update();    
  },

  // single section publish
  onPublishSection : function( data , section){
    var index = findindex(this.resume.sections, {sectionId: data.sectionId});
    this.resume.sections[index] = section;
    this.resume.sections[index].isSupported = this.isSupportedSection(this.resume.sections[index].type);
    if( data.advices != undefined ){
      this.resume.sections[index].advices = [];
      this.resume.sections[index].advices = data.advices;
    }
    if( data.comments != undefined ){
      this.resume.sections[index].comments = [];
      this.resume.sections[index].comments = data.comments;
    }
    this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);

    //don't show if it contains errors
    if(section.error == null)
    	this.remindToShare();
    else{
    	this.notifyError(section.error);
    }
    this.trigger(this.resume);
  },

  onPublishComments : function( comments , sectionId ){
    var index = findindex( this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].comments = comments;
    this.trigger(this.resume);
  },


  onMoveGroupOrder: function( order ){
    var all_section = [];
    order.map(function( section , index ){
        all_section.push( filter(this.resume.all_section,{'type':section.type } )[0] );
    }.bind(this));
    this.resume.all_section = all_section;
    this.makeSectionsLinear( all_section );
  },

  onMoveSectionOrder:function( order , type ){
     this.resume.all_section.map(function( sectionGroup , index ){
        if( sectionGroup.type == type ){
            var section_order = [];
            order.map(function(section , index ){
              section_order.push( filter(sectionGroup.child ,{'sectionId': section.sectionId } )[0] );
            });
            sectionGroup.child = section_order;
        }
     });
     this.makeSectionsLinear( this.resume.all_section );
  },

  onPostSection: function (data) {
    var sectionMeta = filter(sections, {type: data.type});
    var isMultiple = sectionMeta.length > 0 ? sectionMeta[0].isMultiple : false;
    var hasSection = filter(this.resume.sections, {type: data.type}).length > 0;

    if((!isMultiple) && hasSection) {
      var $section = jQuery('.section[rel="' + data.type + '"]');
      $section.find('.edit').click();
      $section.find('textarea, input').focus();
    }
    else {
      if( data.type == 'SummarySection'){
        data.description = null;
      }
      if( data.type == 'SkillsSection' || data.type == 'CareerInterestsSection' ){
        data.tags = [];
      }
      data.newSection = true;
      data.isSupported = this.isSupportedSection(data.type);
      data.sectionPosition = 1;
      this.resume.sections.push(data);
      this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
      this.trigger(this.resume);
    }
  },

  onDeleteNewSection: function(){
    var tempSectionIndex = findindex( this.resume.sections , { newSection : true } );
    this.resume.sections.splice( tempSectionIndex , 1);
    this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
    this.trigger(this.resume);
  },

  onPublishDeleteSection: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections.splice(index, 1);
    this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
    this.trigger(this.resume);
  },

  onPublishComment: function (comment , data ) {
    var index = findindex(this.resume.sections, {sectionId: data.sectionId});
    if (this.resume.sections[index].comments) {
      this.resume.sections[index].comments.push(comment);
    } else {
      this.resume.sections[index].comments = [comment];
    }
    this.resume.sections[index].numberOfComments += 1;
    this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
    this.trigger(this.resume);

  },
  onPublishDeleteComment: function( comment , sectionId ){
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    if (index != -1 ) {
      var commentIndex = findindex(this.resume.sections[index].comments ,{ commentId : comment.commentId });
      if( commentIndex != -1){
        this.resume.sections[index].comments.splice(commentIndex , 1);
        this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
        this.trigger(this.resume);
      }
    }        
  },

  onPublishAdvices : function( advices , sectionId ){
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    var allAdvices = [];

    if( advices.length ){
      advices.map(function(advice){
        if(advice.status == 'pending'){
          allAdvices.push(advice);
        }
      });
      this.resume.sections[index].advices = allAdvices;
      this.trigger(this.resume);
    }
  },

  onPublishSectionAdvice: function( section , advice ){
    var index = findindex(this.resume.sections, {sectionId: section.sectionId });
    if (this.resume.sections[index].advices) {
      this.resume.sections[index].advices.push(advice);
    } else {
      this.resume.sections[index].advices = [advice];
    }
    this.resume.sections[index].showComments = false;
    this.resume.sections[index].showEdits = true;
    this.resume.sections[index].numberOfSuggestedEdits++;
    this.notifyEditSubmission();
    this.update();

  },
  onPublishDeleteAdvice : function( section , advice ){
    var newsection = clone(section);
    if(newsection.numberOfSuggestedEdits != 0){
      newsection.numberOfSuggestedEdits--;
    }
    EditorActions.putSectionForAdvice( newsection , section , advice );
  },
  onPublishMergeAdvice : function( newsection , advice , section ){
    var advice_count = 0;
    if( section.advices != undefined ){
      section.advices.map( function(adv , ind ){
        if( adv.status == 'pending' ){
          advice_count++;
        }
      });
    }
    this.notifyEditMerge();
    if( advice_count > 1 ) advice_count = advice_count -1; // for the merge .
    newsection.numberOfSuggestedEdits = advice_count;
    EditorActions.putSectionForAdvice( newsection , section , advice );
  },
  onPublishSectionForAdvice : function( newSection , section , advice ){
    var index = findindex(this.resume.sections,{sectionId: section.sectionId});
    var adviceIndex = findindex( section ,{ sectionAdviceId :advice.sectionAdviceId });
    this.resume.sections[index] = newSection;
    this.resume.sections[index].isSupported = this.isSupportedSection(this.resume.sections[index].type);
    this.resume.sections[index].advices = [];
    this.resume.sections[index].comments = [];
    if( section.advices != undefined ){
      this.resume.sections[index].advices = clone(section.advices);
      this.resume.sections[index].advices[adviceIndex] = advice;
    }
    if( section.comments != undefined ){
      this.resume.sections[index].comments = clone(section.comments);
    }
    this.update();     
  },

  onEnableEditMode: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].uiEditMode = true;
    this.update();
  },
  onDisableEditMode: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].uiEditMode = false;
    this.update();
  },
  onShowComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = true;
    this.update();
  },
  onHideComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = false;
    this.update();
  },
  onToggleComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showEdits = false;
    this.resume.sections[index].showComments = !this.resume.sections[index].showComments;
    this.update();
  },
  onToggleEdits: function( sectionId ){
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = false;
    this.resume.sections[index].showEdits = !this.resume.sections[index].showEdits;
    this.update();
  },
  onToggleNav: function() {
    this.resume.isNavOpen = !this.resume.isNavOpen;
    this.trigger(this.resume);
  },
  onToggleSorting: function() {
    this.resume.isSorting = !this.resume.isSorting;
    this.trigger(this.resume);
  },
  onTogglePrintModal: function(flag) {
    this.resume.isPrintModalOpen = flag;
    this.trigger(this.resume);
  },
  update: function(){
    this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
    this.trigger(this.resume);
  },
  onSetStatus: function(status) {
    this.resume.status = status;
    this.trigger(this.resume);
  },
  getInitialState: function () {
    return this.resume;
  },
})
