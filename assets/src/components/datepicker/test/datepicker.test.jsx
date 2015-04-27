var Datepicker = require('../index');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('datepicker', function() {

  it('should have a datepicker trigger class', function() {
    var datepicker = TestUtils.renderIntoDocument(<Datepicker><input /></Datepicker>);
    expect(datepicker.getDOMNode().className).toBe('datepicker-trigger');
  });
  
});
