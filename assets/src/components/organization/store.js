var Reflux = require('reflux');
var request = require('superagent');

var SectionsStore = Reflux.createStore({

  listenables: require('./actions'),

  onAddSection: function(type, position) {

    // Set position    
    this.sections.map(function(result) {
      if(result.sectionPosition >= position) {
        result.sectionPosition = result.sectionPosition + 1;
      }
    });

    switch(type) {
      case 'career goal':
          this.sections.push({
              "type": "freeform",
              "title": "career goal",
              "sectionPosition": position,
              "state": "shown",
              "description": ""
          });
        break;
      case 'skills':
        this.sections.push({
            "type": "freeform",
            "title": "skills",
            "sectionPosition": position,
            "state": "shown",
            "description": ""
        });
        break;
      case 'experience':
          this.sections.push({
            "type": "experience",
            "title": "experience",
            "sectionPosition": position,
            "state": "shown",
            "organizationName": "",
            "organizationDescription": "",
            "role": "",
            "startDate": "",
            "endDate": "",
            "isCurrent": false,
            "location": "",
            "roleDescription": "",
            "highlights": ""
        });
        break;
      case 'education':
            this.sections.push({
              "type": "experience",
              "title": "education",
              "sectionPosition": position,
              "state": "shown",
              "organizationName": "",
              "organizationDescription": "",
              "role": "",
              "startDate": "",
              "endDate": "",
              "isCurrent": false,
              "location": "",
              "roleDescription": "",
              "highlights": ""
          });
        break;
    }   
    this.update();
  },

  onEditSection: function() {
    this.update();
  },

  onSaveSection: function() {
    this.update();
  },

  getSections: function() {
    var documentId, self = this,
        pathItems = window.location.pathname.split("/");
        
        if(pathItems.length > 2) {
            documentId = pathItems[2];

            request
              .get('/resume/' + documentId + '/section')
              .set('Accept', 'application/json')
              .end(function(error, res) {

                if (res.ok) {
                  if(res.body.length == 0) {
                    self.sections = '';
                  } else {
                    self.sections =  res.body;
                  }

                  self.update();
                } else {
                  alert(res.text); // this is left intentionally 
                  console.log(res.text); 
                }        
            });
        }

    return this.sections;
  },

  getInitialState: function () {
    this.sections = [];
    
    return {
      sections: this.sections,
    }
  },

  update: function () {
    this.trigger(this.sections)
  }

});

module.exports = SectionsStore;