var GetFeedback = require('../index');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('GetFeedback', function() {
	beforeEach(function () {
    this.server = sinon.fakeServer.create();
    this.server.respondWith("GET", "/togglz-feature//SUGGESTIONS//is-active.json", [200, { "Content-Type": "application/json" }, 'true']);
  });

  it('Should render properly', function() {
    var getFeedback = TestUtils.renderIntoDocument(<GetFeedback />)
    expect(TestUtils.isCompositeComponent(getFeedback)).toBe(true);
  });
});