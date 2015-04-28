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

    it('should return errorMessage as null for valid email', function() {
      var setting = {
        name: 'email', value: 'john@doe.com'
      }
      expect(store.validateField(setting).errorMessage).toBe(null);
    });

    it('should return errorMessage for invalid email', function() {
      var setting = {
        name: 'email', value: 'john'
      }
      expect(store.validateField(setting).errorMessage).toBe('Invalid E-mail');
    });

  });

  describe('Name of the group', function() {
    
  });

});