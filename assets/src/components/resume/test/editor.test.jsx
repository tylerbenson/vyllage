var ResumeEditor = require('../Editor');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Editor', function() {

  beforeEach(function () {
    this.server = sinon.fakeServer.create();
    this.server.respondWith("GET", "/resume//header.json", [200, { "Content-Type": "application/json" }, '']);
    this.server.respondWith("GET", "/resume//section.json", [200, { "Content-Type": "application/json" }, '']);
  });
  
  it('Should render properly', function() {
    var resumeEditor = TestUtils.renderIntoDocument(<ResumeEditor />)
    expect(TestUtils.isCompositeComponent(resumeEditor)).toBe(true);
  });

  it('Should not show subheader if user is not owner', function() {
    var resumeEditor = TestUtils.renderIntoDocument(<ResumeEditor />)
    resumeEditor.setState({resume: {header: {owner: false}}});
    var nodes = TestUtils.scryRenderedDOMComponentsWithClass(resumeEditor, 'subheader');
    expect(nodes.length).toBe(0);
  });
  
});