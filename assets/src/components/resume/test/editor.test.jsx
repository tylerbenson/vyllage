var ResumeEditor = require('../Editor');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Editor', function() {
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