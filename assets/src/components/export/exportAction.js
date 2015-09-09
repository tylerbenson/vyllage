var Reflux = require('reflux');
var request = require('superagent');
var ExportStore = require('./exportStore');

var ExportAction = Reflux.createActions([
  'getAllResumeStyle',
  'grabResumeStyle',
  'changeActive'
]);

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