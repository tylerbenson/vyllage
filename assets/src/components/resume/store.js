var Reflux = require('reflux');
var request = require('superagent');
var endpoints = require('../endpoints');
var urlTemplate = require('url-template');
var findindex = require('lodash.findindex');
var omit = require('lodash.omit');
var assign = require('lodash.assign');
var max = require('lodash.max');
var update = require('react/lib/update');
var sortby = require('lodash.sortby');

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
      documentId: this.documentId,
      header: {
        firstName: '',
        middleName: '',
        lastName: ''
      },
      sections: [],
      sectionOrder: ['career goal', 'experience', 'education', 'skills'],
      isNavOpen: false
    };
  },
  getMaxSectionPostion: function () {
    var section = max(this.resume.sections, 'sectionPosition');
    // Return 0 if sections is empty
    return (section !== -Infinity) ? section.sectionPosition: 0;
  },
  postSectionOrder: function () {
    var order = this.resume.sections.map(function (section) {
      return section.sectionId;
    });
    console.log(order);

    var url = urlTemplate
                .parse(endpoints.resumeSectionOrder)
                .expand({documentId: this.documentId});
    request
      .put(url)
      .set(this.tokenHeader, this.tokenValue)
      .send(order)
      .end(function (err, res) {
        //console.log(err, res.body, order, url);
      }.bind(this))
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
      }.bind(this))
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
            if (section.numberOfComments > 0) {
              this.onGetComments(section.sectionId);
            }
          }.bind(this))
          this.trigger(this.resume);
        }
      }.bind(this))
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
        var newSectionPosition = section.sectionPosition
        console.log('new', newSectionPosition, data);
        this.resume.sections.forEach(function (section) {
          if (section.sectionPosition >= newSectionPosition) {
            section.sectionPosition += 1;
          }
        });
        this.resume.sections.splice(newSectionPosition - 1, 0, section);
        this.postSectionOrder();
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
      .send(omit(data, ['uiEditMode', 'showComments', 'comments', 'newSection']))
      .end(function (err, res) {
        var index = findindex(this.resume.sections, {sectionId: data.sectionId});
        this.resume.sections[index] = res.body;
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
        this.trigger(this.resume);
      }.bind(this));
  },
  onUpdateSectionOrder: function (order) {
    this.resume.sectionOrder = order;
    this.trigger(this.resume);
  },
  onMoveSection: function (id, afterId) {
    const { sections } = this.resume;
    const section = sections.filter(c => c.sectionId === id)[0];
    const afterSection = sections.filter(c => c.sectionId === afterId)[0];
    const index = sections.indexOf(section);
    const afterIndex = sections.indexOf(afterSection);

    var obj = update(this.resume, {
      sections: {
        $splice: [
          [index, 1],
          [afterIndex, 0, section]
        ]
      }
    });
    this.resume.sections = obj.sections;
    this.postSectionOrder();
    this.trigger(this.resume);
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
          this.postSectionOrder();
        } else {
          this.resume.sections[index].comments = [res.body];
        }
        this.resume.sections[index].numberOfComments += 1;
        this.trigger(this.resume);
      }.bind(this))
  },
  onEnableEditMode: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].uiEditMode = true;
    this.trigger(this.resume);
  },
  onDisableEditMode: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].uiEditMode = false;
    this.trigger(this.resume);
  },
  onShowComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = true;
    this.trigger(this.resume);
  },
  onHideComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = false;
    this.trigger(this.resume);
  },
  onToggleComments: function (sectionId) {
    var index = findindex(this.resume.sections, {sectionId: sectionId});
    this.resume.sections[index].showComments = !this.resume.sections[index].showComments;
    console.log(sectionId, index, this.resume.sections[index]);
    this.trigger(this.resume);
  },
  onToggleNav: function() {
    this.resume.isNavOpen = !this.resume.isNavOpen;
    this.trigger(this.resume);
  },
  getInitialState: function () {
    return this.resume;
  },
})
