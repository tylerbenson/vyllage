// jest.dontMock('../src/sum');
var sum = require('../src/sum');

describe('sum', function() {
  it('adds 1 + 2 to equal 3', function() {
    expect(sum(1, 2)).toBe(4);
  });
});