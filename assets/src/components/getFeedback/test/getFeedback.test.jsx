var GetFeedback = require('../index');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('GetFeedback', function() {
  it('Should render properly', function() {
    var getFeedback = TestUtils.renderIntoDocument(<GetFeedback />)
    expect(TestUtils.isCompositeComponent(getFeedback)).toBe(true);
  });
});