var Reflux = require('reflux');

var SuggestionStore = Reflux.createStore({
	listenables: require('./actions'),
	init: function(){
		this.suggestions = [];
	},
	onGetSuggestions: function(){
  	//AJAX here
  	var response = [{
			id: '1',
			name: 'David Greene',
			tagline: 'Helping People Achieve Greater Careers',
			avatar: '/images/avatars/1.jpg',
			is_sponsored: true
		},
		{
			id: '2',
			name: 'Stefanie Reyes',
			tagline: 'Making Change through Strong Leadership',
			avatar: '/images/avatars/2.jpg',
			is_sponsored: false
		},
		{
			id: '3',
			name: 'John Lee',
			tagline: 'Aspiring Project Management Technologist',
			avatar: '/images/avatars/3.jpg',
			is_sponsored: false
		},
		{
			id: '4',
			name: 'Jessica Knight',
			tagline: 'Executive Team Lead',
			avatar: '/images/avatars/4.jpg',
			is_sponsored: false
		},
		{
			id: '5',
			name: 'Carl Jensen',
			tagline: 'Success through Sales',
			avatar: '/images/avatars/5.jpg',
			is_sponsored: true
		}];
  	this.suggestions = response;
  	this.update();
  },
  requestForFeedback: function(index){
  	//AJAX request here

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