var Reflux = require('reflux');

var SuggestionStore = Reflux.createStore({
	listenables: require('./actions'),
	init: function(){
		this.suggestions = [
			{
				title: 'Users matching your Career Interests',
				users: [
					{
						id: '1',
						name: 'Darth Vader',
						tagline: 'I am your father'
					},
					{
						id: '2',
						name: 'R2-D2',
						tagline: '!@#$%^&*()'
					},
					{
						id: '3',
						name: 'C3PO',
						tagline: 'Navigating an asteroid field in approximately 3,720 to 1'
					},
					{
						id: '4',
						name: 'Chubaka',
						tagline: 'Ahrhrhrhh'
					}
				]
			}
		];
	},
  onGetSuggestions: function(){
  	//AJAX here
  	this.update();
  },
  update: function(){
  	this.trigger({
  		suggestions: this.suggestions
  	});
  },
	getInitialState: function(){
		return {
			suggestions: this.suggestions
		}
	}
});

module.exports = SuggestionStore;