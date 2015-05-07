var Freeform = require('../freeform');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Freeform', function() {

  it('should show description', function() {
    var section = { sectionId: 1, description: 'my goal'}
    var freeform = TestUtils.renderIntoDocument(<Freeform section={section} />);
    expect(freeform.getDOMNode().textContent).toMatch(/my goal/);
  });
  
});