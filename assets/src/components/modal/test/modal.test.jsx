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

  it('should call the close function when clicked on overlay', function() {
    var close = jasmine.createSpy('close');
    var modal = TestUtils.renderIntoDocument(<Modal isOpen={true} close={close}>modal content</Modal>)
    TestUtils.Simulate.click(modal.getDOMNode());
    expect(close).toHaveBeenCalled();
  });

  it('should have classes overlay, modal if isOpen = true', function() {
    var modal = TestUtils.renderIntoDocument(<Modal isOpen={true}>modal content</Modal>)
    expect(modal.getDOMNode().className).toBe('overlay');
    var modalCotent = TestUtils.findRenderedDOMComponentWithClass(modal, 'modal')
    expect(modalCotent).toEqual(jasmine.any(Object));
  });

});