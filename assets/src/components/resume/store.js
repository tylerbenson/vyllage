var React = require('react');
var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
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

module.exports = Reflux.createStore({
  listenables: require('./actions'),
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
    this.resume = {
      ownDocumentId: this.documentId,
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
      isPrintModalOpen: false
    };
  },
  remindToShare: function() {
    var message = <span>Your resumé has been updated!  Now, share  it to the world to <a href="/resume/get-feedback">get feedback.</a></span>;
    var timeout = 6000;

    PubSub.publish('banner-alert', {isOpen: true, message: message, timeout: timeout});
  },
  getMaxSectionPostion: function () {
    var section = max(this.resume.sections, 'sectionPosition');
    // Return 0 if sections is empty
    return (section !== -Infinity) ? section.sectionPosition: 0;
  },
  postSectionOrder: function( sections ) {
    var order = sections.map(function (section) {
      return section.sectionId;
    });

    var url = urlTemplate
                .parse(endpoints.resumeSectionOrder)
                .expand({documentId: this.documentId});
    request
      .put(url)
      .set(this.tokenHeader, this.tokenValue)
      .send(order)
      .end(function (err, res) {
        // console.log(err, res.body, order, url);
      }.bind(this))
  },
  onGetDocumentId: function() {
    var url = urlTemplate
              .parse(endpoints.documentId)
              .expand({documentType: 'RESUME'});
    request
      .get(url)
      .end(function (err, res) {
        if(res.ok && res.body.RESUME.length > 0) {
          this.resume.ownDocumentId = res.body.RESUME[0];
          this.resume.documentId = typeof parseInt(this.resume.documentId) === 'number' ? this.resume.documentId : this.resume.ownDocumentId;
          this.trigger(this.resume);
        }
      }.bind(this));
  },
  onGetResume: function () {
    this.onGetHeader();
    this.onGetSections();

  },
  onGetHeader: function () {
    var url = urlTemplate
                .parse(endpoints.resumeHeader)
                .expand({documentId: this.documentId});
    request
      .get(url)
      .set('Accept', 'application/json')
      .end(function (err, res) {
        if (res.ok) {
          this.resume.header = res.body;

          this.trigger(this.resume);
        }
      }.bind(this));
  },
  onUpdateTagline: function (tagline) {
    var url = urlTemplate
                .parse(endpoints.resumeHeader)
                .expand({documentId: this.documentId});
    request
      .put(url)
      .set(this.tokenHeader, this.tokenValue)
      .send({tagline: tagline})
      .end(function (err, res) {
        this.resume.header.tagline = tagline;
        this.trigger(this.resume);
      }.bind(this))
  },
  isSupportedSection: function (type) {
    var supported = ['SummarySection','JobExperienceSection','EducationSection','SkillsSection','CareerInterestsSection'];
    return supported.indexOf(type) > -1;
  },
  onGetSections: function () {
    var url = urlTemplate
                .parse(endpoints.resumeSections)
                .expand({documentId: this.documentId});
    request
      .get(url)
      .set('Accept', 'application/json')
      .end(function (err, res) {
        if (res.ok) {
          this.resume.sections = res.body;
          this.resume.sections = sortby(this.resume.sections, 'sectionPosition');
          // Fetching comments here instead of comments component to avoid infinite loop of api calls to comments
          this.resume.sections.forEach(function (section) {
            section.isSupported = this.isSupportedSection(section.type);
            if (section.numberOfComments > 0) {
              this.onGetComments(section.sectionId);
            }
          }.bind(this));
          this.trigger(this.resume);
        }

      }.bind(this));
  },


  onPublishSections : function( sections , header ){

      this.resume.header = header;
      this.resume.sections = sections;
      this.resume.sections.forEach(function (section) {
        section.isSupported = this.isSupportedSection(section.type);
        if (section.numberOfComments > 0) {
          this.onGetComments(section.sectionId);
        }
      }.bind(this));

     this.resume.all_section = this.doProcessSection( this.resume.sections, header.owner);
     this.trigger(this.resume);

  },

  doProcessSection : function(sections , owner ){
    var tmp_section = [];
    sections = sortby(sections ,'sectionPosition');

    if( sections.length > 0 ){

      sections.forEach(function(section){
          section.isSupported = this.isSupportedSection(section.type);
          if( section.isSupported){
              if( tmp_section.length){
                var findIt = findindex(tmp_section, {'type': section.type});
                 if( findIt == -1){
                    tmp_section.push({
                      type: section.type,
                      title : section.title,
                      id : section.sectionId,
                      owner : owner,
                      child : where( sections, { 'type': section.type })
                    });
                 }
              }else{
                tmp_section.push({
                  type: section.type,
                  title : section.title,
                  id : section.sectionId,
                  owner : owner,
                  child : where( sections, { 'type': section.type })
                });
              }
          }
      }.bind(this));
      return tmp_section;
    }
  },
  onMoveGroupOrder: function( order ){
    var all_section = [];
    order.map(function( section , index ){
        all_section.push( filter(this.resume.all_section,{'type':section.type } )[0] );
    }.bind(this));
    this.resume.all_section = all_section;
    this.makeItLinear( all_section );
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
     this.makeItLinear( this.resume.all_section );
  },
  makeItLinear: function(all_section){
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
     this.postSectionOrder( linear_section );
  },

  onPostSection: function (data) {
    var url = urlTemplate
                .parse(endpoints.resumeSections)
                .expand({
                  documentId: this.documentId,
                });
    request
      .post(url)
      .set(this.tokenHeader, this.tokenValue)
      .send(data)
      .end(function (err, res) {
        var section = assign({}, res.body);
        section.newSection = true;  // To indicate a section is newly created
        section.isSupported = this.isSupportedSection(section.type);

        // section order should reflect into whole thing .
        section.sectionPosition = this.resume.sections.length + 1;
        // get section position .
        this.resume.sections.push(section);
        this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);

        this.trigger(this.resume);
      }.bind(this));
  },
  onPutSection: function (data) {
    var url = urlTemplate
                .parse(endpoints.resumeSection)
                .expand({
                  documentId: this.documentId,
                  sectionId: data.sectionId
                });
    request
      .put(url)
      .set(this.tokenHeader, this.tokenValue)
      .send(omit(data, ['uiEditMode', 'showComments', 'comments', 'newSection', 'isSupported']))
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {sectionId: data.sectionId});
        this.resume.sections[index] = res.body;
        this.resume.sections[index].isSupported = this.isSupportedSection(this.resume.sections[index].type);
        this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);

        this.remindToShare();
        this.trigger(this.resume);
      }.bind(this));
  },
  onDeleteSection: function (sectionId) {
    var url = urlTemplate
                .parse(endpoints.resumeSection)
                .expand({
                  documentId: this.documentId,
                  sectionId: sectionId
                });
    request
      .del(url)
      .set(this.tokenHeader, this.tokenValue)
      .set('Content-Type', 'application/json')
      .send({documentId: this.documentId, sectionId: sectionId})
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {sectionId: sectionId});
        this.resume.sections.splice(index, 1);
        this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
        this.trigger(this.resume);
      }.bind(this));
  },
  onGetComments: function (sectionId) {
    var url = urlTemplate
                .parse(endpoints.resumeComments)
                .expand({
                  documentId: this.documentId,
                  sectionId: sectionId
                });
    request
      .get(url)
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {sectionId: sectionId});
        this.resume.sections[index].comments = res.body;
       // this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
        this.trigger(this.resume);
      }.bind(this))
  },
  onPostComment: function (data) {
    var url = urlTemplate
                .parse(endpoints.resumeComments)
                .expand({
                  documentId: this.documentId,
                  sectionId: data.sectionId
                });
    data = assign({}, data, {
      sectionVersion: 1
    })
    request
      .post(url)
      .set(this.tokenHeader, this.tokenValue)
      .send(data)
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {sectionId: data.sectionId});
        if (this.resume.sections[index].comments) {
          this.resume.sections[index].comments.push(res.body);
        } else {
          this.resume.sections[index].comments = [res.body];
        }
        this.resume.sections[index].numberOfComments += 1;
        this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
        this.trigger(this.resume);
      }.bind(this))
  },
  onDeleteComment: function( comment , sectionId ){
    var url = urlTemplate
                .parse(endpoints.resumeComment)
                .expand({
                  documentId: this.documentId,
                  sectionId: sectionId,
                  commentId: comment.commentId
                });
    request
      .del(url)
      .set(this.tokenHeader, this.tokenValue)
      .set('Content-Type', 'application/json')
      .send(comment)
      .end(function (err, res) {
         if(res.status == 202){
            var index = findindex(this.resume.sections, {sectionId: sectionId});
            if (index != -1 ) {
              var commentIndex = findindex(this.resume.sections[index].comments ,{ commentId : comment.commentId });
              if( commentIndex != -1){
                this.resume.sections[index].comments.splice(commentIndex , 1);
                this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
                this.trigger(this.resume);
              }
            }
         }
      }.bind(this));
  },
  onEnableEditMode: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].uiEditMode = true;
    this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
    this.trigger(this.resume);
  },
  onDisableEditMode: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].uiEditMode = false;
    this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
    this.trigger(this.resume);
  },
  onShowComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = true;
    this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
    this.trigger(this.resume);
  },
  onHideComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = false;
    this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
    this.trigger(this.resume);
  },
  onToggleComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = !this.resume.sections[index].showComments;
    this.resume.all_section = this.doProcessSection( this.resume.sections, this.resume.header.owner);
    // console.log(sectionId, index, this.resume.sections[index]);
    this.trigger(this.resume);
  },
  onToggleNav: function() {
    this.resume.isNavOpen = !this.resume.isNavOpen;
    this.trigger(this.resume);
  },
  onTogglePrintModal: function(flag) {
    this.resume.isPrintModalOpen = flag;
    this.trigger(this.resume);
  },
  getInitialState: function () {
    return this.resume;
  },
})
