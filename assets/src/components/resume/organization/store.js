var Reflux = require('reflux');
var request = require('superagent');

var SectionsStore = Reflux.createStore({

	listenables: require('./actions'),

	onAddSection: function(type, position) {

		// Set position    
		this.sections.map(function(result) {
			if(result.sectionPosition >= position) {
				result.sectionPosition = result.sectionPosition + 1;
			}
		});

		switch(type) {
			case 'career goal':
					this.sections.push({
							"type": "freeform",
							"title": "career goal",
							"sectionPosition": position,
							"state": "shown",
							"description": ""
					});
				break;
			case 'skills':
				this.sections.push({
						"type": "freeform",
						"title": "skills",
						"sectionPosition": position,
						"state": "shown",
						"description": ""
				});
				break;
			case 'experience':
					this.sections.push({
						"type": "experience",
						"title": "experience",
						"sectionPosition": position,
						"state": "shown",
						"organizationName": "",
						"organizationDescription": "",
						"role": "",
						"startDate": "",
						"endDate": "",
						"isCurrent": false,
						"location": "",
						"roleDescription": "",
						"highlights": ""
				});
				break;
			case 'education':
						this.sections.push({
							"type": "experience",
							"title": "education",
							"sectionPosition": position,
							"state": "shown",
							"organizationName": "",
							"organizationDescription": "",
							"role": "",
							"startDate": "",
							"endDate": "",
							"isCurrent": false,
							"location": "",
							"roleDescription": "",
							"highlights": ""
					});
				break;
		}

		this.disableEditMode = true;
		this.update();
	},

	onSaveSection: function(data) {
		var self = this, documentId,
				pathItems = window.location.pathname.split("/"),
				token_header = document.getElementById('meta_header').content,
				token_val = document.getElementById('meta_token').content;

				if(pathItems.length > 2) {
					documentId = pathItems[2];

						// Check for Create mode
					if(!data.sectionId) {
							// Create mode
							request
								.post('/resume/' + documentId + '/section/')
								.set(token_header, token_val)
								.set('Accept', 'application/json')
								.send(data)
								.end(function(error, res) {
									if (res.ok) {
										for(var i = 0; i < self.sections.length; i++){
											if(self.sections[i].sectionPosition === data.sectionPosition){
												self.sections[i] = data;
												break;
											}
										}
										self.disableEditMode = false;
										self.update();
									} else {
										alert(res.text);
										console.log(res.text); 
									}  
								});
					} else {
						// Update mode
						request
							.post('/resume/' + documentId + '/section/' + data.sectionId +'')
							.set(token_header, token_val)
							.set('Accept', 'application/json')
							.send(data)
							.end(function(error, res) {

								if (res.ok) {
									for(var i = 0; i < self.sections.length; i++){
										if(self.sections[i].sectionId === data.sectionId){
												self.sections[i] = data;
												break;
										}
									}
									self.update();
								} else {
									alert(res.text); 
									console.log(res.text); 
								}  
							});
					}
				}
	},

	onDeleteSection: function(sectionId){
		var self = this, documentId,
		pathItems = window.location.pathname.split("/"),
		token_header = document.getElementById('meta_header').content,
		token_val = document.getElementById('meta_token').content;

		if(pathItems.length > 2) {
			documentId = pathItems[2];

				// Check for Create mode
			if(sectionId) {
					request
						.del('/resume/' + documentId + '/section/'+ sectionId)
						.set(token_header, token_val)
						.set('Accept', 'application/json')
						.end(function(error, res) {
							if (res.ok) {
								for(var i = 0; i < self.sections.length; i++){
                  if(self.sections[i].sectionId === sectionId){
                    self.sections.splice(i,1);
                  }
                }
								self.update();
							} else {
								alert(res.text);
								console.log(res.text); 
							}  
				});
			}
		}
	},

	getSections: function() {
		var documentId, self = this,
				pathItems = window.location.pathname.split("/");
				
				if(pathItems.length > 2) {
						documentId = pathItems[2];

						request
							.get('/resume/' + documentId + '/section')
							.set('Accept', 'application/json')
							.end(function(error, res) {

								if (res.ok) {
									if(res.body.length == 0) {
										self.sections = '';
									} else {
										self.sections =  res.body;
									}

									self.update();
								} else {
									alert(res.text); // this is left intentionally 
									console.log(res.text); 
								}        
						});
				}

		return this.sections;
	},

	getDisableState: function(){
		return this.disableEditMode;
	},

	getInitialState: function () {
		this.sections = [];
		this.disableEditMode = false;
		
		return {
			sections: this.sections,
			disableEditMode: this.disableEditMode
		}
	},

	update: function () {
		this.trigger({sections: this.sections,
					  disableEditMode: this.disableEditMode});
	}

});

module.exports = SectionsStore;