var Reflux = require('reflux');

var SuggestionStore = Reflux.createStore({
	listenables: require('./actions'),
	init: function(){
		this.suggestions = [
			{
				id: '1',
				name: 'David Greene',
				tagline: 'Helping People Achieve Greater Careers',
				avatar: '/images/avatars/1.jpg'
			},
			{
				id: '2',
				name: 'Stefanie Reyes',
				tagline: 'Making Change through Strong Leadership',
				avatar: '/images/avatars/2.jpg'
			},
			{
				id: '3',
				name: 'John Lee',
				tagline: 'Aspiring Project Management Technologist',
				avatar: '/images/avatars/3.jpg'
			},
			{
				id: '4',
				name: 'Jessica Knight',
				tagline: 'Executive Team Lead',
				avatar: '/images/avatars/4.jpg'
			},
			{
				id: '5',
				name: 'Carl Jensen',
				tagline: 'Success through Sales',
				avatar: '/images/avatars/5.jpg'
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