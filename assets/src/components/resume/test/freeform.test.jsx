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
    var title = TestUtils.findRenderedDOMComponentWithClass(freeform, 'title');
    expect(title.getDOMNode().textContent).toBe('Goal');
  });

  it('should show message if section is empty', function() {
    var freeform = TestUtils.renderIntoDocument(<Freeform title='Goal' />);
    var content = TestUtils.findRenderedDOMComponentWithClass(freeform, 'content');
    expect(content.getDOMNode().textContent).toBe('No goal added yet');
  });
  
});