var React = require('react');
var Header = require('./components/header');
var Profile = require('./components/profile');
var ContactInfo = require('./components/contact/contact');
var ShareInfo = require('./components/contact/share');
var FreeformContainer = require('./components/freeform/container');
var ArticleContent = require('./components/organization/article-content');
var ArticleControlls =require('./components/freeform/article-controlls');
var CommentsBlog = require('./components/freeform/comments-blog');
var AddSections = require('./components/addSections/addSections');
var request = require('superagent');

var EmptyStateData =[
{
    "type": "freeform",
    "title": "career goal",
    "sectionPosition": 1,
    "state": "shown",
    "description": ""
},
{
    "type": "experience",
    "title": "experience",
    "sectionPosition": 2,
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
}
];

function compare(a,b) {
    
    if (a.sectionPosition < b.sectionPosition){
        return -1;
    }
    if (a.sectionPosition > b.sectionPosition) {
        return 1;
    }
    return 0;
}

var MainContainer = React.createClass({  
    
    getInitialState: function() {
        return {mainData: []};
    },

    componentDidMount : function() {
        var self = this, documentId,
            pathItems = window.location.pathname.split("/");
        
        if(pathItems.length > 2) {
            documentId = pathItems[2];

            request
               .get('/resume/' + documentId + '/section')
               .set('Accept', 'application/json')
               .end(function(error, res) {

                    if (res.ok) {
                        if(res.body.length == 0) {
                            // data is empty eq. registration page, first logged in
                            self.setState({mainData: ''});
                        } else {
                            self.setState({mainData: res.body});
                        }
                    } else {
                        alert(res.text); // this is left intentionally 
                        console.log(res.text); 
                   }             
            });
        } else {
            // this case should not happen, throw error
        }
    },

    componentDidUpdate: function() {

        var resizingTextareas = [].slice.call(document.querySelectorAll('textarea'));

        resizingTextareas.forEach(function(textarea) {
          textarea.addEventListener('input', autoresize, false);
        });

        function autoresize() {
            this.style.height = 0;
            this.style.height = this.scrollHeight  +'px';
        }
    },

    saveChanges: function (data) {
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
                    .send(data)
                    .end(function(error, res) {

                        if (res.ok) {
                            for(var i = 0; i < self.state.mainData.length; i++){

                                if(self.state.mainData[i].sectionPosition === data.sectionPosition){

                                    self.state.mainData[i] = data;
                                    self.setState({mainData: self.state.mainData});
                                    return;
                                }
                            }
                        } else {
                           alert( res.text );  // this is left intentionally
                           console.log(res.text); 
                        }  
                    });
            } else {
                // Update mode
                request
                    .post('/resume/' + documentId + '/section/' + data.sectionId +'')
                    .set(token_header, token_val)
                    .send(data)
                    .end(function(error, res) {

                        if (res.ok) {
                            for(var i = 0; i < self.state.mainData.length; i++){

                                if(self.state.mainData[i].sectionId === data.sectionId){

                                    self.state.mainData[i] = data;
                                    self.setState({mainData: self.state.mainData});
                                    return;
                                }
                            }
                        } else {
                           alert( res.text );  // this is left intentionally
                           console.log(res.text); 
                        }  
                    });
            }
        }
    },

    addSection: function (type,sectionPosition) {
        var  position = sectionPosition,
             data = this.state.mainData;

         // Set position    
        data.map(function(result) {
            if(result.sectionPosition >= position)
            {
                result.sectionPosition = result.sectionPosition + 1;
            }
        });
        
        this.state.editModePosition = position;

        switch(type) {
            case 'career goal':
                    data.push({
                        "type": "freeform",
                        "title": "career goal",
                        "sectionPosition": position,
                        "state": "shown",
                        "description": ""
                    });
                break;
            case 'skills':
                    data.push({
                        "type": "freeform",
                        "title": "skills",
                        "sectionPosition": position,
                        "state": "shown",
                        "description": ""
                    });
                break;
            case 'experience':
                        data.push({
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
                        data.push({
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

        // Sort by sectionPosition
        data.sort(compare);
        this.setState({mainData: data});
    },

    // Get freeForm items
    freeFormItems : function(result,addNewSection,firstElement) {
        // This will be called when clicking on Add Section button, section will be displayed in edit mode
        if(addNewSection) {
            return (
                 <article className="career-goal forceEditMode">
                     <div className="row" id = {result.sectionId}>
                         <div  className="twelve columns">
                            <div className="u-pull-left full">
                              <button className="u-pull-left article-btn"> {result.title} </button>
                              <button className="u-pull-right article-btn addSection-btn" onClick={this.addSection.bind(null, result.title,result.sectionPosition)}> {result.title}</button>
                            </div>
                             
                            <FreeformContainer freeformData={result} saveChanges={this.saveChanges}/>

                            <ArticleControlls/>

                            <CommentsBlog/>  
                        </div>
                    </div>
                </article>)
        }

        // this will be called on page rendering for the first item on each section , section will be displayed in main mode.
        if(firstElement) {
            return (
                 <article className="career-goal ">
                     <div className="row" id = {result.sectionId}>
                         <div  className="twelve columns">
                            <div className="u-pull-left full">
                              <button className="u-pull-left article-btn"> {result.title} </button>
                              <button className="u-pull-right article-btn addSection-btn " onClick={this.addSection.bind(null, result.title,result.sectionPosition)}> {result.title}</button>
                            </div>
                             
                            <FreeformContainer freeformData={result} saveChanges={this.saveChanges}/>

                            <ArticleControlls/>

                            <CommentsBlog/>  
                        </div>
                    </div>
                </article>)
        } else {
            // secondary items on main mode.
            return (
                <article className="career-goal">
                     <div className="row" id = {result.sectionId}>
                         <div className="twelve columns">
                                                                    
                            <FreeformContainer freeformData={result} saveChanges={this.saveChanges}/>

                            <ArticleControlls/>

                            <CommentsBlog/>  
                        </div>
                    </div>
                </article>)
        }

    },

    // Get experience items
    experienceItems : function(result,addNewSection,firstElement)
    {
        // This will be called when clicking on Add Section button, section will be displayed in edit mode
        if(addNewSection) {
             return (
                    <article className="experience forceEditMode">
                        <div className="row">
                             <div className="twelve columns">
                                 <div className="u-pull-left full">
                                    <button className="u-pull-left article-btn"> {result.title} </button>
                                    <button className="u-pull-right article-btn addSection-btn" onClick={this.addSection.bind(null, result.title,result.sectionPosition)}>{result.title}</button>
                                </div>

                                <ArticleContent organizationData={result} saveChanges={this.saveChanges} />

                                <ArticleControlls />

                                <CommentsBlog />
                            </div>
                        </div>
                    </article>)
        }

        // this will be called on page rendering for the first item on each section , section will be displayed in main mode.
        if(firstElement){
            return (
                 <article className="experience">
                    <div className="row">
                         <div className="twelve columns">
                             <div className="u-pull-left full">
                                <button className="u-pull-left article-btn"> {result.title} </button>
                                <button className="u-pull-right article-btn addSection-btn" onClick={this.addSection.bind(null, result.title,result.sectionPosition)}>{result.title}</button>
                            </div>

                            <ArticleContent organizationData={result} saveChanges={this.saveChanges} />

                            <ArticleControlls />

                            <CommentsBlog />
                        </div>
                    </div>
                </article>)
        } else {
            // secondary items on main mode.
            return (
                <article className="experience">
                    <div className="row">
                         <div className="twelve columns">
                                                                                                         
                            <ArticleContent organizationData={result} saveChanges={this.saveChanges} />

                            <ArticleControlls />

                            <CommentsBlog />
                        </div>
                    </div>
                </article>)
        }

    },

    render: function() {
        var results = this.state.mainData,
            that = this, experienceCount = 0,
            educationCount = 0, careergoalCount = 0,
            skillsCount = 0,  addNewItem = 0;

        if(results == '') {
            this.state.mainData = EmptyStateData;
             return (
                <div>
                    { 
                        EmptyStateData.map(function(result) {
                            if(result.type === "freeform"){
                                return that.freeFormItems(that.state.mainData[0], true,true);
                            }
                            else{
                                return that.experienceItems(that.state.mainData[1], true,true); 
                            }

                        })
                    }
                        <AddSections addSection={that.addSection} title={'education'}  position={results.length+1}/>

                        <AddSections addSection={that.addSection} title={'skills'}  position={results.length+1}/>
                   </div>
                );

        } else {
            results.sort(compare);
            return ( 
                <div>{              
                    // Create loop for elements.
                    results.map(function(result) {
                        // Check for item type
                        if(result.type === "freeform"){
                            addNewItem = 0;

                            // Check for find edit item position
                            if(that.state.editModePosition == result.sectionPosition)
                            {
                                // Case when have edit mode item.
                                // Check for find first element (career goal), need for add button.
                                if(result.title == "career goal") {
                                    addNewItem = 1;
                                    careergoalCount = careergoalCount + 1;
                                }

                                // Check for find first element (skills), need for add button.
                                if(result.title == "skills") {
                                    addNewItem = 1;
                                    skillsCount = skillsCount + 1;
                                }
                                
                                // return element
                                return that.freeFormItems(result, true,true);
                            } else {
                                
                                if(careergoalCount == 0  && result.title == "career goal") {
                                    addNewItem = 1;
                                    careergoalCount = careergoalCount + 1;
                                }

                                if(skillsCount == 0 && result.title == "skills") {
                                    addNewItem = 1;
                                    skillsCount = skillsCount + 1;
                                }

                                if (addNewItem == 1) {
                                    addNewItem = 0;
                                    return that.freeFormItems(result,false,true);
                                }
                                else {
                                    return that.freeFormItems(result,false,false);
                                }
                            }
                        }
                        else {
                            addNewItem = 0;

                            if(that.state.editModePosition == result.sectionPosition)
                            {
                                if(result.title == "experience"){
                                    addNewItem = 1;
                                    experienceCount = experienceCount + 1;
                                }

                                if(result.title == "education"){
                                    addNewItem = 1;
                                    educationCount = educationCount + 1;
                                }

                                return that.experienceItems(result, true,true);

                            }else{
                                if(experienceCount == 0 && result.title == "experience"){
                                    addNewItem = 1;
                                    experienceCount = experienceCount + 1;
                                }

                                if(educationCount == 0 && result.title == "education"){
                                    addNewItem = 1;
                                    educationCount = educationCount + 1;
                                }

                                if (addNewItem == 1) {
                                    addNewItem = 0;
                                    return that.experienceItems(result,false,true);
                                } else {
                                    return that.experienceItems(result,false,false);
                                }
                            }
                        }
                        
                        })
                    }

                    <AddSections addSection={this.addSection} title={'education'} position={results.length+1} />

                    <AddSections addSection={this.addSection} title={'skills'} position={results.length+1} />
                </div>
            );
        }
    }
});

React.render(<MainContainer />, document.getElementById('main-container'));




