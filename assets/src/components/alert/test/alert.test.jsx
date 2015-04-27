var Alert = require('../index');
var React = require('react/addons');
var PubSub = require('pubsub-js');

describe('alert', function() {
  it('default state of alert component', function() {
    var TestUtils = React.addons.TestUtils;
    var alert = TestUtils.renderIntoDocument(<Alert />);
    expect(alert.state).toEqual({isOpen: false, message: ''});
  });

  it('Shows message if isOpen state is true', function() {
    var TestUtils = React.addons.TestUtils;
    var alert = TestUtils.renderIntoDocument(<Alert />);
    alert.setState({isOpen: true, message: 'Your data have been saved'});
    expect(alert.getDOMNode().textContent).toBe('Your data have been saved')
  });
});