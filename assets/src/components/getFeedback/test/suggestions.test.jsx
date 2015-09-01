var Suggestions = require('../suggestions/Suggestions');
var SuggestionSidebar = require('../suggestions/SuggestionSidebar');
var SuggestionItem = require('../suggestions/SuggestionItem');
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;

describe('Suggestions', function() {
	var dummyUser = {"firstName": "Ashley", "middleName":"Middle", "lastName": "Benson", "userId": 6, avatarUrl: '/images/user.png'};

	beforeEach(function () {
    this.server = sinon.fakeServer.create();
    this.server.respondWith("GET", "/togglz-feature//SUGGESTIONS//is-active.json", [200, { "Content-Type": "application/json" }, 'true']);
    this.server.respondWith("GET", "/resume//users", [200, { "Content-Type": "application/json" },
    	JSON.stringify({
	      "recent" : [
	        {"firstName": "Tyler", "middleName":"Middle", "lastName": "Benson", "userId": 1, avatarUrl: '/images/user.png'},
	      ],
	      "recommended": [
	        {"firstName": "Ashley", "middleName":"Middle", "lastName": "Benson", "userId": 6, avatarUrl: '/images/user.png'},
	      ]
	    })
    ]);
  });

  it('Tab should render properly', function() {
    var suggestionsTab = TestUtils.renderIntoDocument(<Suggestions />)
    expect(TestUtils.isCompositeComponent(suggestionsTab)).toBe(true);
  });

  it('Sidebar should render properly', function() {
    var suggestionsSidebar = TestUtils.renderIntoDocument(<SuggestionSidebar />)
    expect(TestUtils.isCompositeComponent(suggestionsSidebar)).toBe(true);
  });

  it('Item should have name', function() {
    var suggestionItem = TestUtils.renderIntoDocument(<SuggestionItem user={dummyUser} />);
    var name = TestUtils.findRenderedDOMComponentWithClass(suggestionItem, 'name');
    expect(name).toEqual(jasmine.any(Object));
  });

  it('Item name should match props', function() {
    var suggestionItem = TestUtils.renderIntoDocument(<SuggestionItem user={dummyUser} />);
    var name = TestUtils.findRenderedDOMComponentWithClass(suggestionItem, 'name');
    expect(name.getDOMNode().textContent).toBe('Ashley Benson');
  });

  it('Item should have tagline', function() {
    var suggestionItem = TestUtils.renderIntoDocument(<SuggestionItem user={dummyUser} />);
    var tagline = TestUtils.findRenderedDOMComponentWithClass(suggestionItem, 'tagline');
    expect(tagline).toEqual(jasmine.any(Object));
  });

  it('Item should have avatar', function() {
    var suggestionItem = TestUtils.renderIntoDocument(<SuggestionItem user={dummyUser} />);
    var avatar = TestUtils.scryRenderedDOMComponentsWithClass(suggestionItem, 'avatar');
    expect(avatar[0]).toEqual(jasmine.any(Object));
  });

  it('Item avatar should match avatarUrl', function() {
    var suggestionItem = TestUtils.renderIntoDocument(<SuggestionItem user={dummyUser} />);
    var avatar = TestUtils.scryRenderedDOMComponentsWithClass(suggestionItem, 'avatar');
    var bg = avatar[0].getDOMNode().style.backgroundImage;
    expect(bg).toEqual('url(http://localhost:9876/images/user.png)');
  });

  it('Item avatar should match avatar dimensions', function() {
    var suggestionItem = TestUtils.renderIntoDocument(<SuggestionItem user={dummyUser} avatarSize="48" />);
    var avatar = TestUtils.scryRenderedDOMComponentsWithClass(suggestionItem, 'avatar');
    var height = avatar[0].getDOMNode().style.height;
    var width = avatar[0].getDOMNode().style.width;
    expect([height, width]).toEqual(['48px','48px']);
  });
});