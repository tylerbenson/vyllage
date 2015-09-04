var Alert = require('../index');
var React = require('react/addons');
var PubSub = require('pubsub-js');
var TestUtils = React.addons.TestUtils;

describe('alert', function() {

  it('default state of alert component', function() {
    var alert = TestUtils.renderIntoDocument(<Alert />);
    expect(alert.state).toEqual({isOpen: false, message: '', timeout: 4000});
  });

  it('should have default className alert', function() {
    var alert = TestUtils.renderIntoDocument(<Alert />);
    expect(alert.getDOMNode().className).toEqual('alert');
  });

  it('should be able to set custom className', function() {
    var alert = TestUtils.renderIntoDocument(<Alert className='success' />);
    expect(alert.getDOMNode().className).toEqual('success');
  });

  it('Shows message if isOpen state of is true', function() {
    var alert = TestUtils.renderIntoDocument(<Alert />);
    alert.setState({isOpen: true, message: 'Your data have been saved'});
    expect(alert.getDOMNode().textContent).toBe('Your data have been saved')
  });

  it('should have visible className if alert is open', function() {
    var alert = TestUtils.renderIntoDocument(<Alert />);
    alert.setState({isOpen: true});
    expect(alert.getDOMNode().className).toEqual('visible alert');
  });

  it('Clicking on close button of alert should close it', function() {
    var alert = TestUtils.renderIntoDocument(<Alert />);
    alert.setState({isOpen: true, message: 'Your data have been saved', timeout: 4000});
    var closeButton = TestUtils.findRenderedDOMComponentWithTag(alert, 'button');
    TestUtils.Simulate.click(closeButton);
    expect(alert.state.isOpen).toEqual(false);
    expect(alert.getDOMNode().textContent).toBe('');
  });

  it('should listen to published events', function() {
  });

});

