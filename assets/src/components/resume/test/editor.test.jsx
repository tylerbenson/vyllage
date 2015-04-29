var ResumeEditor = require('../Editor');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Editor', function() {
  it('Should render properly', function() {
    var resumeEditor = TestUtils.renderIntoDocument(<ResumeEditor />)
    expect(TestUtils.isCompositeComponent(resumeEditor)).toBe(true);
  });
  
});