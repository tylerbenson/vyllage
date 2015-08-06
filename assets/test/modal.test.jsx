var Modal = require('./../src/components/modal');
var React = require('react/addons');

describe('modal', function() {
  it('basic modal test', function() {
    var TestUtils = React.addons.TestUtils;
    var modal = TestUtils.renderIntoDocument(<Modal />);
    expect(TestUtils.isCompositeComponent(modal)).toBe(true);
  });
});