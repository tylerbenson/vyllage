var Banner = require('../banner');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Banner', function() {

  it('should render properly', function() {
    var banner = TestUtils.renderIntoDocument(<Banner />)
    expect(TestUtils.isCompositeComponent(banner)).toBe(true);
  });

  it('should render name', function() {
    var header = {
      firstName: 'John',
      lastName: 'Doe'
    }
    var banner = TestUtils.renderIntoDocument(<Banner header={header}/>)
    expect(banner.getDOMNode().textContent).toMatch(/John  Doe/)
  });

  it('should render tagline', function() {
    var header = {
      tagline: 'my tagline'
    }
    var banner = TestUtils.renderIntoDocument(<Banner header={header}/>)
    expect(banner.getDOMNode().textContent).toMatch(/my tagline/)
  });

  it('should render address', function() {
    var header = {
      address: 'united states'
    }
    var banner = TestUtils.renderIntoDocument(<Banner header={header}/>)
    expect(banner.getDOMNode().textContent).toMatch(/united states/)
  });

  // it('should render phoneNumber', function() {
  //   var header = {
  //     phoneNumber: 123456789
  //   }
  //   var banner = TestUtils.renderIntoDocument(<Banner header={header}/>)
  //   expect(banner.getDOMNode().value).toMatch(/123456789/)
  // });

  // it('should render email', function() {
  //   var header = {
  //     email: 'john@doe.com'
  //   }
  //   var banner = TestUtils.renderIntoDocument(<Banner header={header}/>)
  //   expect(banner.getDOMNode().textContent).toMatch(/john@doe.com/)
  // });

  // it('should render twitter username', function() {
  //   var header = {
  //     twitter: 'johndoe'
  //   }
  //   var banner = TestUtils.renderIntoDocument(<Banner header={header}/>)
  //   expect(banner.getDOMNode().value).toMatch(/johndoe/)
  // });

  
});