var Modal = require('../index');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('modal', function() {

  it('should be empty if isOpen is false', function() {
    var modal = TestUtils.renderIntoDocument(<Modal>modal content</Modal>)
    expect(modal.getDOMNode().textContent).toBe('');
  });

  it('should have modal content if isOpen is true', function() {
    var modal = TestUtils.renderIntoDocument(<Modal isOpen={true}>modal content</Modal>)
    expect(modal.getDOMNode().textContent).toBe("modal content");
  });

});