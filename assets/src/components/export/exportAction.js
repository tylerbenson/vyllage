var Reflux = require('reflux');
var request = require('superagent');
var ExportStore = require('./exportStore');
var endpoints = require('../endpoints');
var urlTemplate = require('url-template');


var ExportAction = Reflux.createActions([
  'checkForOwner',
  'resultForOwner',
  'getAllResumeStyle',
  'grabResumeStyle',
  'changeActive',
  'togglePrintModal'
]);

ExportAction.checkForOwner.preEmit = function(){
    var documentId = window.location.pathname.split('/')[2];
    var header;
    var headerUrl = urlTemplate
                .parse(endpoints.resumeHeader)
                .expand({documentId: documentId});
    request
      .get(headerUrl)
      .set('Accept', 'application/json')
      .end(function (err, res) {
        if( res.status == 200 && res.body.owner != undefined){
          ExportAction.resultForOwner(res.body.owner);
        }else{
          window.location.replace('/');
        }
      });
}

ExportAction.getAllResumeStyle.preEmit = function(){
	request
      .get('/resume/file/pdf/styles')
      .set('Accept', 'application/json')
      .end(function (err, res) {
				if( res.body.length ){
					ExportAction.grabResumeStyle(res.body);
				}
      });
}

module.exports = ExportAction;