var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var Profile = require('../Profile');

describe('profile', function() {

  it('should have role', function() {
  	var settings = [
  		{'name': 'role', 'value': 'student', privacy: 'public'}
  	]
    var profile = TestUtils.renderIntoDocument(<Profile settings={settings} />);
    expect(profile.refs.role.getDOMNode().value).toEqual('student');
  });

});