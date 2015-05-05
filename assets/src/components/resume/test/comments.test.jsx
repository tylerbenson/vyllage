var Comments = require('../comments');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Comments', function() {

  it('Should render properly', function() {
    var comments = TestUtils.renderIntoDocument(<Comments />)
    expect(TestUtils.isCompositeComponent(comments)).toBe(true);
  });

  it('Should be null if showComments is false', function() {
    var section = {
      showComments: false
    }
    var comments = TestUtils.renderIntoDocument(<Comments section={section} />)
    expect(comments.getDOMNode()).toBeNull();
  });

  it('Should show comments', function() {
    var section = {
      sectionId: 1,
      showComments: true,
      comments: [{
        userName: '',
        commentText: 'first comment',
        lastModified: new Date()
      }]
    }
    var comments = TestUtils.renderIntoDocument(<Comments section={section} />)
    expect(comments.getDOMNode().textContent).toMatch(/first comment/);
  });
  
});