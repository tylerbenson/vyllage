var path = require('path');
var ApiMock = require('api-mock/lib/api-mock');

var apiMock = new ApiMock({
  'blueprintPath': path.resolve('./api/sample.md'),
  'options': {}
});

apiMock.run();