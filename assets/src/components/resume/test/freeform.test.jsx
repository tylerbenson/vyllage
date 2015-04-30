var Freeform = require('../freeform');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Freeform', function() {

  it('should show message if section and title is empty', function() {
    var freeform = TestUtils.renderIntoDocument(<Freeform />);
    expect(freeform.getDOMNode().textContent).toBe('No  added yet');
  });

  it('should have title', function() {
    var freeform = TestUtils.renderIntoDocument(<Freeform title='Goal' />);
    expect(freeform.getDOMNode().textContent).toMatch(/^Goal/);
  });

  it('should show message if section is empty', function() {
    var freeform = TestUtils.renderIntoDocument(<Freeform title='Goal' />);
    expect(freeform.getDOMNode().textContent).toMatch(/No goal added yet$/);
  });

  it('should show description', function() {
    var section = { sectionId: 1, description: 'my goal'}
    var freeform = TestUtils.renderIntoDocument(<Freeform section={section} />);
    expect(freeform.getDOMNode().textContent).toMatch(/my goal/);
  });
  
});