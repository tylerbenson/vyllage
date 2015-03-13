var Reflux = require('reflux');
var request = require('superagent');

var SectionsStore = Reflux.createStore({

  listenables: require('./actions'),

  onAddSection: function() {
    this.update();
  },

  onEditSection: function() {
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