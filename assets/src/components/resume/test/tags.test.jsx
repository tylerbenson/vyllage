var Tags = require('../tags');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Tags', function() {

  it('should render properly', function() {
    var section = { sectionId: 1, tags: ['a','b','c']}
    var tags = TestUtils.renderIntoDocument(<Tags section={section} />);
    expect(TestUtils.isCompositeComponent(tags)).toBe(true);
  });
});