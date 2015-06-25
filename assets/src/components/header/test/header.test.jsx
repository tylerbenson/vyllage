var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var Header = require('../index');

describe('header', function() {

  it('should have logo', function() {
    var header = TestUtils.renderIntoDocument(<Header />);
    var logo = TestUtils.findRenderedDOMComponentWithClass(header, 'logo');
    expect(logo).toEqual(jasmine.any(Object));
  });

  it('should have page-title', function() {
    var header = TestUtils.renderIntoDocument(<Header />);
    var pageTitle = TestUtils.findRenderedDOMComponentWithClass(header, 'page-title');
    expect(pageTitle).toEqual(jasmine.any(Object));
  });

  // it('should have user name', function() {
  //   var header = TestUtils.renderIntoDocument(<Header />);
  //   var name = TestUtils.findRenderedDOMComponentWithClass(header, 'name');
  //   expect(name).toEqual(jasmine.any(Object));
  // });

  it('page title should match value passed from prop', function() {
    var header = TestUtils.renderIntoDocument(<Header title={'new title'} />);
    var pageTitle = TestUtils.findRenderedDOMComponentWithClass(header, 'page-title');
    expect(pageTitle.getDOMNode().textContent).toBe('new title');
  });

  // it('user name should match value passed from prop', function() {
  //   var header = TestUtils.renderIntoDocument(<Header name={'john'} />);
  //   var name = TestUtils.findRenderedDOMComponentWithClass(header, 'name');
  //   expect(name.getDOMNode().textContent).toBe('john');
  // });

});

