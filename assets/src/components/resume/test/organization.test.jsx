var Organization = require('../organization');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Organization', function() {

  it('should render properly', function() {
    var organization = TestUtils.renderIntoDocument(<Organization section={{}} />)
    expect(TestUtils.isCompositeComponent(organization)).toBe(true);
  });

  // it('should show title', function() {
  //   var section = {
  //     organizationName: 'corp'
  //   }
  //   var organization = TestUtils.renderIntoDocument(<Organization section={section} />)
  //   expect(organization.getDOMNode()).toMatch(/corp/)
  // });

});