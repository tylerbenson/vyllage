var store = require('../store');

describe('settings store', function() {

  describe('validateField', function() {

    it('should return errorMessage as null for valid linkedIn url', function() {
      var setting = {
        name: 'linkedIn', value: 'http://linkedin.com/john'
      }
      expect(store.validateField(setting).errorMessage).toBe(null);
    });

    it('should return errorMessage for invalid likedIn url', function() {
      var setting = {
        name: 'linkedIn', value: 'john'
      }
      expect(store.validateField(setting).errorMessage).toBe('Invalid URL');
    });

  });

});