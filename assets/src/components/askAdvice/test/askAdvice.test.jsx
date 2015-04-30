var AskAdvice = require('../index');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('AskAdvice', function() {
  it('Should render properly', function() {
    var askAdvice = TestUtils.renderIntoDocument(<AskAdvice />)
    expect(TestUtils.isCompositeComponent(askAdvice)).toBe(true);
  });
});