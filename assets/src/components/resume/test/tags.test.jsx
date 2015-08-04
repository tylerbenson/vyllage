var Tags = require('../tags');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Tags', function() {

  it('should show tags', function() {
    var section = { sectionId: 1, tags: ['a','b','c']}
    var tags = TestUtils.renderIntoDocument(<Tags section={section} />);
    expect(tags.getDOMNode().textContent).toMatch(/a, b, c/);
  });

});