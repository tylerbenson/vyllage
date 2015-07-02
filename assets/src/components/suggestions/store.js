var Reflux = require('reflux');

var SuggestionStore = Reflux.createStore({
	listenables: require('./actions'),
	init: function(){
		this.suggestions = [
			{
				id: '1',
				name: 'David Greene',
				tagline: 'Helping People Achieve Greater Careers'
			},
			{
				id: '2',
				name: 'Stefanie Reyes',
				tagline: 'Making Change through Strong Leadership'
			},
			{
				id: '3',
				name: 'John Lee',
				tagline: 'Aspiring Project Management Technologist'
			},
			{
				id: '4',
				name: 'Jessica Knight',
				tagline: 'Executive Team Lead'
			},
			{
				id: '5',
				name: 'Carl Jensen',
				tagline: 'Success through Sales'
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